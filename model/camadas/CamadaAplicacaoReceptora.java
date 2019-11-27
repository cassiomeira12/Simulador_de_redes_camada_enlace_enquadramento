/***********************************************************************
* Autor: Cassio Meira Silva
* Matricula: 201610373
* Inicio: 01/02/18
* Ultima alteracao: 17/02/18
* Nome: CamadaAplicacaoReceptora
* Funcao: Converter um vetor de inteiros para uma String (mensagem) com
          base nos valores da tabela ASCII de cada caractere
***********************************************************************/

package model.camadas;

import view.Painel;


public class CamadaAplicacaoReceptora extends Thread {
  private final String nomeDaCamada = "CAMADA APLICACAO RECEPTORA";
  private final int velocidade = 300;

  private int[] quadro;

  public void run() {
    System.out.println(nomeDaCamada);
    try {

      Painel.CAMADAS_RECEPTORAS.expandirCamadaAplicacao();

      String mensagem = "";//Mensagem que foi recebida
      char[] arrayCaracteres = new char[quadro.length];//Vetor de Caracteres da Mensagem

      //Adicionando o caractere referente ao valor inteiro da Tabela [ASCII]
      for (int i=0; i<quadro.length; i++) {
        arrayCaracteres[i] = (char) quadro[i];//Adicionando o caractere com base no codigo ASCII
        System.out.println("\tCaractere ["+arrayCaracteres[i]+"] = " + quadro[i]);
        Painel.CAMADAS_RECEPTORAS.camadaAplicacao("Caractere ["+arrayCaracteres[i]+"] = [ASCII] " + quadro[i] + "\n");
        mensagem += arrayCaracteres[i];//Concatenando caractere na String
        Thread.sleep(velocidade);
      }

      Painel.CAMADAS_RECEPTORAS.camadaAplicacao("\nMensagem: [" + mensagem + "]\n\n");
      Thread.sleep(velocidade);

      Painel.CAMADAS_RECEPTORAS.terminouCamada();

      chamarProximaCamada(mensagem);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*********************************************
  * Metodo: camadaAplicacaoReceptora
  * Funcao: Converter uma String para um vetor de Inteiros
  * Parametros: mensagem : String
  * Retorno: void
  *********************************************/
  public void camadaAplicacaoReceptora(int[] quadro) {
    this.quadro = quadro;
    this.start();//Iniciando a Thread dessa Camada
  }

  /*********************************************
  * Metodo: chamarProximaCamada
  * Funcao: Passa os dados a serem enviados dessa Camada para a proxima
  * Parametros: mensagem : String
  * Retorno: void
  *********************************************/
  private void chamarProximaCamada(String mensagem) throws Exception {
    AplicacaoReceptora.aplicacaoReceptora(mensagem);
  }

}//Fim class