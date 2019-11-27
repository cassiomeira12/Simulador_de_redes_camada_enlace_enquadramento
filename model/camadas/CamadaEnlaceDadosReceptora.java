/***********************************************************************
* Autor: Cassio Meira Silva
* Matricula: 201610373
* Inicio: 06/03/18
* Ultima alteracao: 14/03/18
* Nome: CamadaEnlaceDadosReceptora
* Funcao: Receber um Quadro, processar e enviar para a proxima Camada
***********************************************************************/

package model.camadas;

import view.Painel;
import util.ManipuladorDeBit;


public class CamadaEnlaceDadosReceptora extends Thread {
  private final String nomeDaCamada = "CAMADA ENLACE DE DADOS RECEPTORA";
  private final int velocidade = 300;//Velocidade de Sleep

  private int[] quadro;//Informacao recebida da Camada Superior

  public void run() {
    System.out.println(nomeDaCamada);
    try {

      Painel.CAMADAS_RECEPTORAS.expandirCamadaEnlace();

      //quadro = camadaEnlaceReceptoraControleDeFluxo(quadro);
      //quadro = camadaEnlaceReceptoraControleDeErro(quadro);
      quadro = camadaEnlaceReceptoraEnquadramento(quadro);

      chamarProximaCamada(quadro);
      Painel.CONFIGURACOES.setDisable(false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*********************************************
  * Metodo: camadaEnlaceDadosReceptora
  * Funcao: Recebe a informacao da Camada Fisica e inicia o processamento desta Camada
  * Parametros: quadro : int[]
  * Retorno: void
  *********************************************/
  public void camadaEnlaceDadosReceptora(int[] quadro) {
    this.quadro = quadro;
    this.start();//Iniciando a Thread dessa Camada
  }

  /*********************************************
  * Metodo: chamarProximaCamada
  * Funcao: Passa os dados a serem enviados dessa Camada para a proxima
  * Parametros: quadro : int[]
  * Retorno: void
  *********************************************/
  private void chamarProximaCamada(int... quadro) throws Exception {
    new CamadaAplicacaoReceptora().camadaAplicacaoReceptora(quadro);
  }
  
  /*********************************************
  * Metodo: camadaEnlaceReceptorEnquadramento
  * Funcao: Divide o Quadro recebido em informacoes de Carga Util
  * Parametros: quadro : int[]
  * Retorno: quadroDesenquadrado : int[]
  *********************************************/
  private int[] camadaEnlaceReceptoraEnquadramento(int[] quadro) throws Exception {
    System.out.println("\tENQUADRAMENTO");
    Painel.CAMADAS_RECEPTORAS.camadaEnlace("ENQUADRAMENTO\n");
    Thread.sleep(velocidade);

    int[] quadroDesenquadrado = null;

    int tipoDeEnquadramento = Painel.CONFIGURACOES.enquadramento.getIndiceSelecionado();

    switch(tipoDeEnquadramento) {
      case 0:
        quadroDesenquadrado = enquadramentoContagemDeCaracteres(quadro);
        break;
      case 1:
        quadroDesenquadrado = enquadramentoInsercaoDeBytes(quadro);
        break;
      case 2:
        quadroDesenquadrado = enquadramentoInsercaoDeBits(quadro);
        break;
      case 3:
        quadroDesenquadrado = enquadramentoViolacaoCamadaFisica(quadro);
        break;
    }

    Painel.CAMADAS_RECEPTORAS.camadaEnlace("\n");

    return quadroDesenquadrado;
  }

  /*********************************************
  * Metodo: enquadramentoContagemDeCaracteres
  * Funcao: Dividir a mensagem em quadros levando em consideracao o espaco entre as palavras (" ")
  * Parametros: quadro : int[]
  * Retorno: quadroEnquadrado : int[]
  *********************************************/
  private int[] enquadramentoContagemDeCaracteres(int[] quadro) throws Exception {
    System.out.println("\n\t[Contagem de Caracteres]");
    Painel.CAMADAS_RECEPTORAS.camadaEnlace("\n\t[Contagem de Caracteres]\n");
    Thread.sleep(velocidade);

    int informacaoDeControle = ManipuladorDeBit.getPrimeiroByte(quadro[0]);//Quantidade de Bits do quadro
    //Quantidade de Bits de carga util do quadro
    int quantidadeDeBitsCargaUtil = informacaoDeControle;
    System.out.println("IC: " + informacaoDeControle);
    Painel.CAMADAS_RECEPTORAS.camadaEnlace("\n\tIC ["+informacaoDeControle+"] ");


    int novoTamanho = quantidadeDeBitsCargaUtil/8;

    int[] quadroDesenquadrado = new int[novoTamanho];//Novo vetor de Carga Util
    int posQuadro = 0;//Posicao do Vetor de Quadros

    int cargaUtil = 0;//Nova Carga Util

    quadro[0] = ManipuladorDeBit.deslocarBits(quadro[0]);//Deslocando os bits 0's a esquerda
    //Primeiro inteiro do Quadro - Contem a informacao de Controle IC nos primeiros 8 bits
    quadro[0] <<= 8;//Deslocando 8 bits para a esquerda, descartar a IC

    Painel.CAMADAS_RECEPTORAS.camadaEnlace("Carga Util [ ");
    for (int i=1; (i<=3) && (i<=novoTamanho); i++) {
      cargaUtil = ManipuladorDeBit.getPrimeiroByte(quadro[0]);
      quadroDesenquadrado[posQuadro++] = cargaUtil;
      Painel.CAMADAS_RECEPTORAS.camadaEnlace(cargaUtil + " ");
      quadro[0] <<= 8;//Desloca 8 bits para a esquerda
    }

    Thread.sleep(velocidade);

    //Caso o quadro for composto por mais de um inteiro do vetor
    for (int i=1, quantidadeByte; posQuadro<novoTamanho; i++) {
      quantidadeByte = ManipuladorDeBit.quantidadeDeBytes(quadro[i]);
      quadro[i] = ManipuladorDeBit.deslocarBits(quadro[i]);

      for (int x=1; (x<=quantidadeByte) && (x<=4); x++) {
        cargaUtil = ManipuladorDeBit.getPrimeiroByte(quadro[i]);
        quadroDesenquadrado[posQuadro++] = cargaUtil;
        Painel.CAMADAS_RECEPTORAS.camadaEnlace(cargaUtil + " ");
        quadro[i] <<= 8;//Desloca 8 bits para a esquerda
      }
      Thread.sleep(velocidade);
    }
    
    Painel.CAMADAS_RECEPTORAS.camadaEnlace("]\n");
    Thread.sleep(velocidade);

    return quadroDesenquadrado;
  }

  /*********************************************
  * Metodo: enquadramentoInsercaoDeBytes
  * Funcao: Dividir a mensagem em quadros adicionando o byte [S] no inicio do quadro e o byte [E] no final
  * Parametros: quadro : int[]
  * Retorno: quadroEnquadrado : int[]
  *********************************************/
  private int[] enquadramentoInsercaoDeBytes(int[] quadro) throws Exception {
    System.out.println("\n\t[Insercao de Bytes]");
    Painel.CAMADAS_RECEPTORAS.camadaEnlace("\n\t[Insercao de Bytes]");
    Thread.sleep(velocidade);

    final char byteFlagStart = 'S';//Identificar o INICIO do quadro (Start)
    final char byteFlagEnd = 'E';//Identificar o FIM do quadro (End)
    final char byteDeEscape = '/';//Caractere de escape especial

    Painel.CAMADAS_RECEPTORAS.camadaEnlace("\n\tByte de Inicio de Quadro ["+byteFlagStart+"]");
    Painel.CAMADAS_RECEPTORAS.camadaEnlace("\n\tByte de Fim de Quadro ["+byteFlagEnd+"]");
    Painel.CAMADAS_RECEPTORAS.camadaEnlace("\n\tByte de Escape de Quadro ["+byteDeEscape+"]\n");
    Thread.sleep(velocidade);


    String auxiliar = "";
    Boolean SE = true;

    for (int inteiro : quadro) {

      int quantidadeByte = ManipuladorDeBit.quantidadeDeBytes(inteiro);
      inteiro = ManipuladorDeBit.deslocarBits(inteiro);

      int inteiroByte = ManipuladorDeBit.getPrimeiroByte(inteiro);

      Painel.CAMADAS_RECEPTORAS.camadaEnlace("\n\tIC ["+(char) inteiroByte+"] ");
      Thread.sleep(velocidade);

      if (inteiroByte == (int) byteFlagStart) {//Inicio do Quadro
        SE = !SE;//Iniciar a Busca pelo Byte de Fim de Quadro
        inteiro <<= 8;//Deslocando 8 bits para a esquerda
        quantidadeByte--;
      }

      if (!SE) {

        for (int i=1; i<=quantidadeByte; i++) {
          int dado = ManipuladorDeBit.getPrimeiroByte(inteiro);

          if (dado == (int) byteDeEscape) {//Verificando se o dado eh um Byte de Escape
            inteiro <<= 8;//Deslocando 8 bits para a esquerda
            Painel.CAMADAS_RECEPTORAS.camadaEnlace("IC ["+(char) dado+"] ");
            Thread.sleep(velocidade);
            dado = ManipuladorDeBit.getPrimeiroByte(inteiro);//Adicionando o Byte
            auxiliar += (char) dado;
            Painel.CAMADAS_RECEPTORAS.camadaEnlace("Carga Util ["+dado+"] ");
            Thread.sleep(velocidade);
            inteiro <<= 8;//Deslocando 8 bits para a esquerda
            i++;

          } else if (dado == (int) byteFlagEnd) {//Verificando se o dado eh um Byte End
            SE = !SE;//Encontrou o Byte de Fim de Quadro
            Painel.CAMADAS_RECEPTORAS.camadaEnlace("IC ["+(char) dado+"]\n");
            Thread.sleep(velocidade);
          } else {//Caso for um Byte de Carga Util
            auxiliar += (char) ManipuladorDeBit.getPrimeiroByte(inteiro);
            inteiro <<= 8;//Deslocando 8 bits para a esquerda
            Painel.CAMADAS_RECEPTORAS.camadaEnlace("Carga Util ["+dado+"] ");
            Thread.sleep(velocidade);
          }
        
        }

      }
    }

    //Novo Quadro de Carga Util
    int[] quadroDesenquadrado = new int[auxiliar.length()];
    //Adicionando as informacoes de Carga Util no QuadroDesenquadrado
    for (int i=0; i<auxiliar.length(); i++) {
      quadroDesenquadrado[i] = (int) auxiliar.charAt(i);
      ManipuladorDeBit.imprimirBits(quadroDesenquadrado[i]);
    }

    return quadroDesenquadrado;
  }

  /*********************************************
  * Metodo: enquadramentoInsercaoDeBits
  * Funcao: Dividir o quadro recebido em Carga Util
  * Parametros: quadro : int[]
  * Retorno: quadroDesenquadrado : int[]
  *********************************************/
  private int[] enquadramentoInsercaoDeBits(int[] quadro) throws Exception {
    System.out.println("\n\t[Insercao de Bits]");
    Painel.CAMADAS_RECEPTORAS.camadaEnlace("\n\t[Insercao de Bits]\n");
    Thread.sleep(velocidade);

    //Byte Flag que contem a sequencia de bits "0111110"
    final int byteFlag = 126;//00000000 00000000 00000000 01111110
    Painel.CAMADAS_RECEPTORAS.camadaEnlace("\t[Bits de Flag [01111110] = 126]\n\n");
    Thread.sleep(velocidade);

    String auxiliar = "";
    Boolean SE = true;

    for (int inteiro : quadro) {
      //System.out.println("Quadro");
      int quantidadeByte = ManipuladorDeBit.quantidadeDeBytes(inteiro);
      ManipuladorDeBit.imprimirBits(inteiro);
      //System.out.println("Bits deslocados");
      inteiro = ManipuladorDeBit.deslocarBits(inteiro);
      ManipuladorDeBit.imprimirBits(inteiro);
      int inteiroByte = ManipuladorDeBit.getPrimeiroByte(inteiro);

      if (inteiroByte == byteFlag) {//Inicio do Quadro
        SE = !SE;//Iniciar a Busca pelo Byte de Fim de Quadro
        inteiro <<= 8;//Deslocando 8 bits para a esquerda
        quantidadeByte--;
        Painel.CAMADAS_RECEPTORAS.camadaEnlace("\tIC [" + inteiroByte + "] ");
      }

      if (!SE) {

        ManipuladorDeBit.imprimirBits(inteiro);

        for (int i=1; i<=quantidadeByte; i++) {
          int dado = ManipuladorDeBit.getPrimeiroByte(inteiro);
          System.out.println("asdfasdf\n");
          ManipuladorDeBit.imprimirBits(dado);

          if (dado == byteFlag) {//Verificando se encontrou o Byte de Flag
           SE = !SE;//Encontrou o Byte de Fim de Quadro
          } else {
            Painel.CAMADAS_RECEPTORAS.camadaEnlace("Carga Util [ ");

            int novoQuadro = 0;

            Boolean cincoBits1 = ManipuladorDeBit.cincoBitsSequenciais(dado,1);
            if (cincoBits1) {

              dado = ManipuladorDeBit.deslocarBits(dado);
              ManipuladorDeBit.imprimirBits(dado);

              //Cria um inteiro com 1 no bit mais a esquerda e 0s em outros locais
              int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
              //Para cada bit exibe 0 ou 1
              for (int b=1, cont=0; b<=8; b++) {
                //Utiliza displayMask para isolar o bit
                int bit = (dado & displayMask) == 0 ? 0 : 1;

                if (cont == 5) {
                  cont = 0;//Zerando o contador
                  dado <<= 1;//Desloca 1 bit para a esquerda
                  bit = (dado & displayMask) == 0 ? 0 : 1;
                }

                if (b == 8) {//Quando chegar no Ultimo Bit
                  inteiro <<= 8;//Deslocando 8 bits para esquerda
                  dado = ManipuladorDeBit.getPrimeiroByte(inteiro);
                  dado = ManipuladorDeBit.deslocarBits(dado);
                  ManipuladorDeBit.imprimirBits(dado);

                  novoQuadro <<= 1;//Deslocando 1 bit para a esquerda
                  novoQuadro |= ManipuladorDeBit.pegarBitNaPosicao(dado,1);//Adicionando o bit ao novoDado

                  ManipuladorDeBit.imprimirBits(novoQuadro);

                  auxiliar += (char) novoQuadro;
                  Painel.CAMADAS_RECEPTORAS.camadaEnlace(novoQuadro + " ]\n");
                  Thread.sleep(velocidade);
                  i++;

                } else {//Colocando o Bit no novoQuadro
                  novoQuadro <<= 1;//Deslocando 1 bit para a esquerda
                  novoQuadro |= bit;//Adicionando o bit ao novoDado
                  dado <<= 1;//Desloca 1 bit para a esquerda
                }

                if (bit == 1) {//Quando for um bit 1
                  cont++;
                } else {//Caso vinher um bit 0
                  cont = 0;
                }
              }

            } else {//Caso nao tem uma sequencia de 5 Bits 1's
              auxiliar += (char) dado;
              Painel.CAMADAS_RECEPTORAS.camadaEnlace(dado + " ]\n");
              Thread.sleep(velocidade);
            }
          }
          
          inteiro <<= 8;//Deslocando 8 bits para a esquerda;
        }

      }
    }

    //Novo Quadro de Carga Util
    int[] quadroDesenquadrado = new int[auxiliar.length()];
    //Adicionando as informacoes de Carga Util no QuadroDesenquadrado
    for (int i=0; i<auxiliar.length(); i++) {
      quadroDesenquadrado[i] = (int) auxiliar.charAt(i);
      ManipuladorDeBit.imprimirBits(quadroDesenquadrado[i]);
    }

    return quadroDesenquadrado;
  }

  /*********************************************
  * Metodo: enquadramentoViolacaoCamadaFisica
  * Funcao: Violar a Camada Fisica para que ela determine o inicio e o fim de um quadro
  * Parametros: quadro : int[]
  * Retorno: quadro : int[]
  *********************************************/
  private int[] enquadramentoViolacaoCamadaFisica(int[] quadro) throws Exception {
    System.out.println("\n\t[Violacao da Camada Fisica]");
    Painel.CAMADAS_RECEPTORAS.camadaEnlace("\n\t[Violacao da Camada Fisica]");
    Thread.sleep(velocidade);

    final int byteFlag = 255;//00000000 00000000 00000000 11111111
    Painel.CAMADAS_RECEPTORAS.camadaEnlace("\n\t[Bits de Flag [11111111] = 255]\n\n");
    Thread.sleep(velocidade);

    String auxiliar = "";
    Boolean SE = true;

    for (int inteiro : quadro) {
      int quantidadeByte = ManipuladorDeBit.quantidadeDeBytes(inteiro);
      ManipuladorDeBit.imprimirBits(inteiro);
      inteiro = ManipuladorDeBit.deslocarBits(inteiro);
      ManipuladorDeBit.imprimirBits(inteiro);
      int inteiroByte = ManipuladorDeBit.getPrimeiroByte(inteiro);

      if (inteiroByte == byteFlag) {//Inicio do Quadro
        SE = !SE;//Iniciar a Busca pelo Byte de Fim de Quadro
        inteiro <<= 8;//Deslocando 8 bits para a esquerda
        quantidadeByte--;
        Painel.CAMADAS_RECEPTORAS.camadaEnlace("\tIC [" + inteiroByte + "] ");
      }

      if (!SE) {

        ManipuladorDeBit.imprimirBits(inteiro);

        for (int i=1; i<=quantidadeByte; i++) {
          int dado = ManipuladorDeBit.getPrimeiroByte(inteiro);
          ManipuladorDeBit.imprimirBits(dado);

          if (dado == byteFlag) {//Verificando se encontrou o Byte de Flag
           SE = !SE;//Encontrou o Byte de Fim de Quadro
          } else {
            Painel.CAMADAS_RECEPTORAS.camadaEnlace("Carga Util [ ");

            int novoQuadro = 0;

            Boolean cincoBits1 = ManipuladorDeBit.cincoBitsSequenciais(dado,1);
            if (cincoBits1) {

              dado = ManipuladorDeBit.deslocarBits(dado);
              ManipuladorDeBit.imprimirBits(dado);

              //Cria um inteiro com 1 no bit mais a esquerda e 0s em outros locais
              int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
              //Para cada bit exibe 0 ou 1
              for (int b=1, cont=0; b<=8; b++) {
                //Utiliza displayMask para isolar o bit
                int bit = (dado & displayMask) == 0 ? 0 : 1;

                if (cont == 5) {
                  cont = 0;//Zerando o contador
                  dado <<= 1;//Desloca 1 bit para a esquerda
                  bit = (dado & displayMask) == 0 ? 0 : 1;
                }

                if (b == 8) {//Quando chegar no Ultimo Bit
                  inteiro <<= 8;//Deslocando 8 bits para esquerda
                  dado = ManipuladorDeBit.getPrimeiroByte(inteiro);
                  dado = ManipuladorDeBit.deslocarBits(dado);
                  ManipuladorDeBit.imprimirBits(dado);

                  novoQuadro <<= 1;//Deslocando 1 bit para a esquerda
                  novoQuadro |= ManipuladorDeBit.pegarBitNaPosicao(dado,1);//Adicionando o bit ao novoDado

                  ManipuladorDeBit.imprimirBits(novoQuadro);

                  auxiliar += (char) novoQuadro;
                  Painel.CAMADAS_RECEPTORAS.camadaEnlace(novoQuadro + " ]\n");
                  Thread.sleep(velocidade);
                  i++;

                } else {//Colocando o Bit no novoQuadro
                  novoQuadro <<= 1;//Deslocando 1 bit para a esquerda
                  novoQuadro |= bit;//Adicionando o bit ao novoDado
                  dado <<= 1;//Desloca 1 bit para a esquerda
                }

                if (bit == 1) {//Quando for um bit 1
                  cont++;
                } else {//Caso vinher um bit 0
                  cont = 0;
                }
              }

            } else {//Caso nao tem uma sequencia de 5 Bits 1's
              auxiliar += (char) dado;
              Painel.CAMADAS_RECEPTORAS.camadaEnlace(dado + " ]\n");
              Thread.sleep(velocidade);
            }
          }
          
          inteiro <<= 8;//Deslocando 8 bits para a esquerda;
        }

      }
    }

    //Novo Quadro de Carga Util
    int[] quadroDesenquadrado = new int[auxiliar.length()];
    //Adicionando as informacoes de Carga Util no QuadroDesenquadrado
    for (int i=0; i<auxiliar.length(); i++) {
      quadroDesenquadrado[i] = (int) auxiliar.charAt(i);
      ManipuladorDeBit.imprimirBits(quadroDesenquadrado[i]);
    }

    return quadroDesenquadrado;
  }

}//Fim class