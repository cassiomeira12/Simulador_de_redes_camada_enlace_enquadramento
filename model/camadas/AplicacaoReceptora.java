/***********************************************************************
* Autor: Cassio Meira Silva
* Matricula: 201610373
* Inicio: 01/02/18
* Ultima alteracao: 17/02/18
* Nome: AplicacaoReceptora
* Funcao: Receber o envio de uma Mensagem da Camda de Aplicacao Receptora
***********************************************************************/

package model.camadas;

import view.Painel;


public class AplicacaoReceptora {


  /*********************************************
  * Metodo: aplicacaoReceptora
  * Funcao: Envia uma String (Mensagem) para a Interface Grafica
  * Parametros: mensagem : String
  * Retorno: void
  *********************************************/
  public static void aplicacaoReceptora(String mensagem) {
    System.out.println("\nAPLICACAO RECEPTORA");
    try {

      chamarProximaCamada(mensagem);

    } catch (Exception e) {
      System.out.println("[ERRO] - Aplicacao Receptora");
      e.printStackTrace();
    }
  }

  /*********************************************
  * Metodo: chamarProximaCamada
  * Funcao: Passa os dados a serem enviados dessa Camada para a proxima
  * Parametros: mensagem : String
  * Retorno: void
  *********************************************/
  private static void chamarProximaCamada(String mensagem) throws Exception {
    Painel.COMPUTADOR_RECEPTOR.adicionarMensagem(mensagem);//Adicionando mensagem na interface grafica
  }

}//Fim class