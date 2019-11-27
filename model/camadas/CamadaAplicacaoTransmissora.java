/***********************************************************************
* Autor: Cassio Meira Silva
* Matricula: 201610373
* Inicio: 01/02/18
* Ultima alteracao: 13/03/18
* Nome: CamadaAplicacaoTransmissora
* Funcao: Converter uma String (Mensagem) para um vetor de inteiros com 
          base nos valores da tabela ASCII de cada caractere 
***********************************************************************/

package model.camadas;

import view.Painel;


public class CamadaAplicacaoTransmissora extends Thread {
  private final String nomeDaCamada = "CAMADA APLIACAO TRANSMISSORA";
  private final int velocidade = 300;

  private String mensagem;

  public void run() {
    System.out.println(nomeDaCamada);
    try {

      Painel.CAMADAS_TRANSMISSORAS.limparTextoCamadas();
      Painel.CAMADAS_TRANSMISSORAS.expandirCamadaAplicacao();
      Painel.CAMADAS_TRANSMISSORAS.camadaAplicacao("Mensagem: [" + mensagem + "]\n\n");
      Thread.sleep(velocidade);

      //Vetor de Caracteres da Mensagem
      char[] arrayCaracteres = mensagem.toCharArray();
      //Vetor pra armazenar os valores da Tabela [ASCII] de cada Caractere
      int[] quadro = new int[mensagem.length()];

      //Convertendo os caracteres em valores inteiros referente ao codigo ASCII
      for (int i=0; i<arrayCaracteres.length; i++) {
        quadro[i] = (int) arrayCaracteres[i];//Adicionando o codigo [ASCII] de cada Caractere
        Painel.CAMADAS_TRANSMISSORAS.camadaAplicacao("Caractere ["+arrayCaracteres[i]+"] = ASCII [" + quadro[i] + "]\n");
        Thread.sleep(velocidade);
      }

      chamarProximaCamada(quadro);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*********************************************
  * Metodo: camadaAplicacaoTransmissora
  * Funcao: Converter uma String para um vetor de Inteiros
  * Parametros: mensagem : String
  * Retorno: void
  *********************************************/
  public void camadaAplicacaoTransmissora(String mensagem) {
    this.mensagem = mensagem;
    this.start();//Iniciando a Thread dessa Camada
  }

  /*********************************************
  * Metodo: chamarProximaCamada
  * Funcao: Passa os dados a serem enviados dessa Camada para a proxima
  * Parametros: quadro : int[]
  * Retorno: void
  *********************************************/
  private void chamarProximaCamada(int[] quadro) throws Exception {
    new CamadaEnlaceDadosTransmissora().camadaEnlaceDadosTransmissora(quadro);
  }

}//Fim class