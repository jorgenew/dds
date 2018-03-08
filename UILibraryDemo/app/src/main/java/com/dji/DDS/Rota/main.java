/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dji.DDS.Rota;

/**
 *
 * @author JORGE
 */
public class main {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        
        // TODO code application logic here
        try{ 
            Dijkistra dij = new Dijkistra(8); 
            dij.insertArc(0, 1, 10); 
            dij.insertArc(0, 2, 5); 
            dij.insertArc(1, 2, 2); 
            dij.insertArc(1, 3, 1); 
            dij.insertArc(2, 1, 3); 
            dij.insertArc(2, 3, 9); 
            dij.insertArc(2, 4, 2); 
            dij.insertArc(3, 4, 4);         
            dij.insertArc(4, 3, 6); 
            dij.insertArc(4, 0, 7);    
            dij.insertArc(4, 5, 1);
            dij.insertArc(5, 6, 1);
            dij.insertArc(3, 7, 1);
            
            System.out.println("Menor caminho entre 0 e 4 "+dij.dijkistra(5,7));
            System.out.println(dij.getFilaDePrioridade());
            System.out.println("\n");
       
            dij.printMatriz(dij.getMatrizDePesos()); 
            dij.getFilaDePrioridade();
            
            
            dij.rota(7, 0);
            /*for(int i=0 ; i<10;i++){
                System.out.println(i+"--"+dij.disAnterior[i]);
            }*/
            
             
        }catch(Exception ex){ 
            if(ex.getMessage() == null) 
                System.out.println("Ocorreu um erro de "+ex+" no main"); 
            else  
                System.out.println(ex.getMessage()+"XXX"); 
        } 
    }
    
}
