/***********************************************************************
* Autor: Cassio Meira Silva
* Matricula: 201610373
* Inicio: 18/01/18
* Ultima alteracao: 17/02/18
* Nome: Informacoes
* Funcao: Armazena as informacoes de descricao deste trabalho
***********************************************************************/

package view.sobre;


public class Informacoes {

  public static String NOME = "Cassio Meira Silva";
  public static String MATRICULA = "201610373";
  public static String DISCIPLINA = "Redes de Computadores I";

  public static String TRABALHO = "Simulador de Redes de Computadores - Camada Enlace de Dados";

  private static String trabalho01 = "Implementacao da [Camada Fisica]\n" +
                                    "Algoritmos implementados:\n\n" +
                                    "Codificacao Binaria\n" +
                                    "\tSinal 1 [Alto] - Sinal 0 [Baixo]\n\n" +
                                    "Codificacao Manchester\n" +
                                    "\tSinal 1 [Alto|Baixo] - Sinal 0 [Baixo|Alto]\n\n" +
                                    "Codificacao Manchester Diferencial\n" +
                                    "\tSinal Inicial Anterior definido como [Baixo]\n\n";


  private static String trabalho02 = "Implementacao da [Camada Enlace de Dados]\n" +
                                    "Algoritmos implementados:\n\n" +
                                    "ENQUADRAMENTO\n" +
                                    "\tInsercao de Caracteres\n" +
                                    "\tinsercao de Bytes\n" +
                                    "\tInsercao de Bits\n" +
                                    "\tViolacao da Camada Fisica\n\n";

  public static String DESCRICAO = trabalho02 +"\n\n"+  trabalho01;

}//Fim class