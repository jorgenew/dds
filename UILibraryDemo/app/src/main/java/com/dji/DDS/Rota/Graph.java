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
public class Graph {
    public final int NUMERO_DE_NODOS; 
    public final int OO = 2147483647;//infinito 
    private Integer[][]matrizDePesos; 
         
    public Graph(int numeroDeNodos)throws Exception{ 
         
        if(numeroDeNodos <= 0) 
            throw new Exception("o numero de nodos deve ser maior que 0"); 
        NUMERO_DE_NODOS = numeroDeNodos;     
        try{ 
            setMatrizDePesos(criaMatrizInt(numeroDeNodos,0));                 
        }catch(Exception ex){ 
            if(ex.getMessage() == null) 
                  System.out.println("Ocorreu um erro de "+ex+" no construtor"); 
            else 
                System.out.println(ex.getMessage()); 
        } 
         
    }     
     
 
    public Integer[][] criaMatrizInt(int tamanho, Integer valorPadrao)throws Exception{         
 
        if(tamanho <=1 ) 
            throw new Exception("o tamanho deve ser maior que 1"); 
        //como o grafo vai considerar valores de 1-n 
        Integer matriz[][] = new Integer[tamanho+1][]; 
        try{ 
            for(int i = 0 ; i < tamanho ; i++){ 
                matriz[i] = new Integer[tamanho+1];             
                for(int j = 0 ; j < tamanho ; j++){ 
                    matriz[i][j] = valorPadrao; 
                } 
            } 
        }catch(Exception ex){ 
            if(ex.getMessage() == null) 
                  System.out.println("Ocorreu um erro de "+ex+" em criaMatrizInt"); 
            else 
                System.out.println("Erro ao criar a matriz"); 
        } 
        return matriz; 
    } 
     
    public void printMatriz(Integer matriz[][])throws Exception{ 
         
        if(matriz == null)  
            throw new Exception("a matriz e nula");  
         
        if(matriz[0] == null)  
            throw new Exception("a matriz nao foi inicializada"); 
         
            int tamanho = getNUMERO_DE_NODOS(); 
         
            for(int i = 0 ; i < tamanho ; i++){ 
                for(int j = 0 ; j < tamanho ; j++) 
                    System.out.printf("["+matriz[i][j]+"] "); 
                System.out.printf("\n"); 
            }         
         
    } 
     
    public void insertArc(int A, int B, int peso)throws Exception{ 
        try{ 
            if(A < 0 || B < 0  
                    || A > getNUMERO_DE_NODOS()             
                    || B > getNUMERO_DE_NODOS()) 
                throw new Exception("um dos vertices sao invalidos"); 
            if(peso == 0) 
                throw new Exception("nao eh permitido peso negativo"); 
 
            matrizDePesos[A][B] = peso;     
        }catch(Exception ex){ 
            if(ex.getMessage() == null) 
                System.out.println("Ocorreu um erro de "+ex+" insertArc"); 
            else 
                System.out.println("Erro na insecao de arco"); 
        } 
    } 
     
    public Integer[][] getMatrizDePesos() {         
        return this.matrizDePesos; 
    } 
 
    public void setMatrizDePesos(Integer[][] pesos) {         
        this.matrizDePesos = pesos;         
    } 
 
    public int getNUMERO_DE_NODOS() { 
        return NUMERO_DE_NODOS; 
    } 
}
