/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.crudhibernate;

import com.mycompany.crudhibernate.App;
import com.mycompany.crudhibernate.App;
import com.mycompany.crudhibernate.HibernateUtil;
import com.mycompany.crudhibernate.HibernateUtil;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import models.Pedidos;
import org.hibernate.Session;
import org.hibernate.Transaction;
/**
 * FXML Controller class
 *
 * @author chris
 */
public class HacerPedidoController implements Initializable {


    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtEstado;
    @FXML
    private Spinner<Integer> producto;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnRealizar;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       SpinnerValueFactory svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10,1,1);
       producto.setValueFactory(svf);
    }    
    
    @FXML
    private void volver(ActionEvent event) { 
           try {
            App.setRoot("primary");
        } catch (IOException ex) {
            Logger.getLogger(HacerPedidoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

     @FXML
    private void realizarPedido(ActionEvent event) {
        Pedidos p = new Pedidos();
        
        java.util.Date ahora = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(ahora.getTime());
        
        p.setId(0L);
        p.setCliente(txtNombre.getText());
        p.setFecha(sqlDate);
        p.setEstado(txtEstado.getText());
        p.setProductoId(producto.getValue());
        
        Session s = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = s.beginTransaction();
        s.save(p);
        tr.commit();
    }

}
