package com.mycompany.crudhibernate;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.sql.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SortEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import models.Pedidos;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class SecondaryController implements Initializable {

    @FXML
    private TableView<Pedidos> tabla;
    @FXML
    private TableColumn<Pedidos, Long> colId;
    @FXML
    private TableColumn<Pedidos, String> colCliente;
    @FXML
    private TableColumn<Pedidos, Date> colFecha;
    @FXML
    private TableColumn<Pedidos, String> colEstado;
    @FXML
    private TableColumn<Pedidos, String> colProducto;
    @FXML
    private Label hora;
    @FXML
    private Label pendientes;
    @FXML
    private Button btnVolver;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        ObservableList<Pedidos> contenido = FXCollections.observableArrayList();
//        tabla.setItems(contenido);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("productoId"));
        
        actualizarTabla();

        refresco();
        count();
    }

    private void actualizarTabla() throws HibernateException {
        java.util.Date ahora = new java.util.Date();
        java.sql.Date sqlFecha = new java.sql.Date(ahora.getTime());
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.createQuery("FROM Pedidos p WHERE p.fecha=:fecha", Pedidos.class);
        q.setParameter("fecha", sqlFecha);
        ArrayList<Pedidos> pedidos = (ArrayList<Pedidos>) q.list();
        tabla.getItems().clear();
        tabla.getItems().addAll(pedidos);
        s.close();
    }

    private void refresco() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        java.util.Date fecha = new java.util.Date();
                        hora.setText(fecha.toString());
                        actualizarTabla();
                        count();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 5000);
    }

    private void count() {
        java.util.Date ahora = new java.util.Date();
        java.sql.Date sqlFecha = new java.sql.Date(ahora.getTime());
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.createQuery("SELECT count(*) FROM Pedidos p WHERE p.estado='Pendiente' and p.fecha=:fecha");
        q.setParameter("fecha", sqlFecha);
        pendientes.setText("Quedan " + q.uniqueResult() + " pedidos pendientes");
    }

    @FXML
    private void cambiarEstado(MouseEvent event) {
        
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();
        if (event.getButton() == MouseButton.SECONDARY) {
            eliminar(event);
        } else if (event.getButton() == MouseButton.PRIMARY) {
            //Pedidos p = s.load(Pedidos.class,tabla.getSelectionModel().getSelectedItem() );
            Pedidos p = tabla.getSelectionModel().getSelectedItem();

            p.setEstado("Recogido");
            s.update(p);
            tr.commit();
            s.close();

            actualizarTabla();

        }
    }

    @FXML
    private void volver(ActionEvent event) {
        try {
            App.setRoot("primary");
        } catch (IOException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void eliminar(MouseEvent event) {
        Pedidos p = tabla.getSelectionModel().getSelectedItem();
        System.out.println(p);
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();

        s.remove(p);
        tr.commit();
        s.close();
        
        actualizarTabla();
    }
}
