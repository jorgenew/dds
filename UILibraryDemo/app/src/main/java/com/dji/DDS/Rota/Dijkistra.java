
package com.dji.DDS.Rota;
import java.util.List; 
import java.util.ArrayList; 
import java.util.Iterator; 
/**
 *
 * @author JORGE
 */
public class Dijkistra  extends Graph {
  private boolean nodosVisitados[]; 
    public int distancias[];
    public int disAnterior[];
    public int father[]; 
    private List filaDePrioridade = new ArrayList<Integer>(); 
    private int origem; 
     int rota [] = new int[20];
     
 
    /* 
     * Construtor : recebe o numero de nodos do grafo 
     */ 
    public Dijkistra(int numeroDeNodos)throws Exception{ 
        super(numeroDeNodos); 
        //como sera considerado de 1- n entao e acrescentado + 1 
        nodosVisitados = new boolean[numeroDeNodos]; 
        //como sera considerado de 1- n entao e acrescentado + 1 
        distancias = new int[numeroDeNodos];
        disAnterior = new int[numeroDeNodos]; 
        //antecesor do vertice 
        father = new int[numeroDeNodos]; 
    } 
     
    int dijkistra(int origem, int destino)throws Exception{ 
 
        if(origem < 0 || origem > getNUMERO_DE_NODOS()) 
            throw new Exception("origem eh menor que 0 ou destino nao existe"); 
                 
        try{ 
            iniciaMenorCaminho(origem); 
        }catch(Exception ex){ 
            System.out.println("Erro ao iniciar os dados "+ex); 
        } 
         
                 
        while(!filaDePrioridade.isEmpty()){             
            Integer verticeMenorPeso = extraiMenor();     
             
             
            for(int i = 0 ; i < getNUMERO_DE_NODOS() ; i++){ 
                 
                if(getMatrizDePesos()[verticeMenorPeso][i] > 0)                     
                    relaxa(verticeMenorPeso,i);                 
            } 
             
        } 
        printDistancias(); 
        return distancias[destino];                           
    } 
    /* 
     * Inicia dados do algoritmo 
     */ 
    private void iniciaMenorCaminho(int origem){ 
        for(int i = 0 ; i < getNUMERO_DE_NODOS(); i++){ 
            distancias[i] = OO; 
            nodosVisitados[i] = false;     
            father[i] = OO; 
            filaDePrioridade.add(new Integer(i));//adiciona a aresta na fila 
        } 
        distancias[origem] = 0;//inicia o vetor de distancias 
         
    } 
     
    /* 
     * Relaxa arestas no grafo 
     */ 
    private void relaxa(int u, int v){
        
            if (distancias[v] > distancias[u]+getMatrizDePesos()[u][v]){                 
                distancias[v] = distancias[u]+getMatrizDePesos()[u][v];
                disAnterior[v] = u;
                System.out.println(v+"--"+disAnterior[v]);
                /*if(v==6){
                    System.out.print(u+" l "+v+"  ");
                    System.out.println(distancias[u]+" u "+getMatrizDePesos()[u][v]);
                }*/
                
                
                father[v] = u;                         
            } 
    } 
     
    private int extraiMenor(){ 
        
        int menorValor = OO;    
        int verticeDeMenorPeso=0; 
         int i=0;
        Iterator<Integer>it = filaDePrioridade.iterator(); 
        while(it.hasNext()){ 
            int verticeAtual = it.next();             
            if(distancias[verticeAtual] < menorValor){                 
                menorValor = distancias[verticeAtual]; 
                i++;
                
                verticeDeMenorPeso = verticeAtual;
                
                
            } 
        } 
         
        System.out.println("Remove o vertice "+verticeDeMenorPeso+" da fila " 
                           +" de peso "+menorValor);
        //remove o vertice com menor distancia do grafo 
       
        filaDePrioridade.remove(new Integer(verticeDeMenorPeso)); 
        System.out.println("-------------------------------------------------"); 
         
        return verticeDeMenorPeso; 
    } 
     
    public int getOrigem() { 
        return origem; 
    } 
 
    public void setOrigem(int origem) { 
        this.origem = origem; 
    } 
     
    public List getFilaDePrioridade() { 
        return filaDePrioridade; 
    } 
 
    public void setFilaDePrioridade(List filaDePrioridade) { 
        this.filaDePrioridade = filaDePrioridade; 
    } 
     
    private void printDistancias(){ 
        for(int i = 0 ; i < getNUMERO_DE_NODOS(); i++) 
            System.out.print("["+distancias[i]+"] "); 
        System.out.println(); 
    } 
    
    public void rota(int destino,int origem){
        if(distancias[destino] <0){
            System.out.println("nao ha rota");
        }
        else{
        int contador = destino;
        int i = 1;
        rota [0] = destino; 
        System.out.println(rota[0]);
        while(origem!=contador){
            
            rota[i] = disAnterior[contador];
            System.out.println(disAnterior[contador]);
            contador = disAnterior[contador];
            i++;
            
        }
        }
        
    }
}
