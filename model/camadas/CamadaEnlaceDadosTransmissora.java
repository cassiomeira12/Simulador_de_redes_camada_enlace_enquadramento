/***********************************************************************
* Autor: Cassio Meira Silva
* Matricula: 201610373
* Inicio: 06/03/18
* Ultima alteracao: 14/03/18
* Nome: CamadaEnlaceDadosTransmissora
* Funcao: Dividir a Mensagem em Quadros menores
***********************************************************************/

package model.camadas;

import view.Painel;
import util.ManipuladorDeBit;

import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;


public class CamadaEnlaceDadosTransmissora extends Thread {
  private final String nomeDaCamada = "CAMADA ENLACE DE DADOS TRANSMISSORA";
  private final int velocidade = 300;//Velocidade de Sleep
  
  public static Semaphore SEMAPHORO_QUADRO = new Semaphore(1);//Semaphoro para enviar cada quadro por vez

  private int[] quadro;//Informacao recebida da Camada Superior

  public void run() {
    System.out.println(nomeDaCamada);
    try {
      Painel.CONFIGURACOES.setDisable(true);//Desativando a mudanca das opcoes
      Painel.CAMADAS_TRANSMISSORAS.expandirCamadaEnlace();
      this.imprimirBitsCadaInteiro(quadro);

      Quadro[] quadroEnquadrado;

      quadroEnquadrado = camadaEnlaceTransmissoraEnquadramento(quadro);
      //quadro = camadaEnlaceTransmissoraControleDeErro(quadro);
      //quadro = camadaEnlaceTransmissoraControleDeFluxo(quadro);

      
      for (int i=0; i<quadroEnquadrado.length; i++) {
        SEMAPHORO_QUADRO.acquire();
        Painel.CAMADAS_TRANSMISSORAS.expandirCamadaEnlace();
        Painel.CONFIGURACOES.setDisable(true);//Desativando a mudanca das opcoes
        Thread.sleep(1000);
        quadroEnquadrado[i].setId(i+1);
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\tENVIANDO QUADRO " + quadroEnquadrado[i].getId());
        Thread.sleep(2000);
        chamarProximaCamada(quadroEnquadrado[i].getBits());
      }


    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*********************************************
  * Metodo: camadaEnlaceDadosTransmissora
  * Funcao: Recebe a informacao da Camada Superior e inicia o processamento desta Camada
  * Parametros: quadro : int[]
  * Retorno: void
  *********************************************/
  public void camadaEnlaceDadosTransmissora(int[] quadro) {
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
    new CamadaFisicaTransmissora().camadaFisicaTransmissora(quadro);
  }

  /*********************************************
  * Metodo: imprimirBitsCadaInteiro
  * Funcao: Imprimir na Interface Grafica o Inteiro e os Bits que o compoe
  * Parametros: quadro : int[]
  * Retorno: void
  *********************************************/
  private void imprimirBitsCadaInteiro(int[] quadro) throws Exception {
    System.out.println("\n\tBits de cada Inteiro");
    Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("BITS DE CADA INTEIRO\n");
    for (int c : quadro) {
      System.out.print("\tInteiro ["+c+"] - ");
      if (c > 99) {
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("["+c+"] " + ManipuladorDeBit.imprimirBits(c) + "\n");
      } else {
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("["+c+"]   " + ManipuladorDeBit.imprimirBits(c) + "\n");
      }
      Thread.sleep(velocidade);
    }
  }

  /*********************************************
  * Metodo: camadaEnlaceTransmissoraEnquadramento
  * Funcao: Divide a informacao recebida da Camada Superior em Quadros
  * Parametros: quadro : int[]
  * Retorno: quadroEnquadrado : Quadro[]
  *********************************************/
  private Quadro[] camadaEnlaceTransmissoraEnquadramento(int[] quadro) throws Exception {
    System.out.println("\n\tENQUADRAMENTO");
    Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\nENQUADRAMENTO\n");
    Thread.sleep(velocidade);

    Quadro[] quadroEnquadrado = null;

    //Recebendo o enquadramento escolhido pelo usuario
    int tipoDeEnquadramento = Painel.CONFIGURACOES.enquadramento.getIndiceSelecionado();

    switch(tipoDeEnquadramento) {
      case 0:
        quadroEnquadrado = enquadramentoContagemDeCaracteres(quadro);
        break;
      case 1:
        quadroEnquadrado = enquadramentoInsercaoDeBytes(quadro);
        break;
      case 2:
        quadroEnquadrado = enquadramentoInsercaoDeBits(quadro);
        break;
      case 3:
        quadroEnquadrado = enquadramentoViolacaoCamadaFisica(quadro);
        break;
    }

    Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n");

    return quadroEnquadrado;
  }

  /*********************************************
  * Metodo: enquadramentoContagemDeCaracteres
  * Funcao: Realiza o enquadramento contando uma quantidade de Caracteres
  * Parametros: quadro : int[]
  * Retorno: quadroEnquadrado : Quadro[]
  *********************************************/
  private Quadro[] enquadramentoContagemDeCaracteres(int[] quadro) throws Exception {
    System.out.println("\n\t[Contagem de Caracteres]");
    Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\t[Contagem de Caracteres]\n");
    Thread.sleep(velocidade);
    
    //Quantidade de Bytes de CargaUtil em cada Quadro = Cada Quadro tem no maximo 32 bits
    final int quantidadeDeCaracteres = 3;//24 de bits de carga util (IC)

    int quantidadeDeBytes = 0;//Armazenar a quantidade Bytes que tem no vetor de Inteiros
    for (int inteiro : quadro) {//Verificando o vetor de Inteiros
      quantidadeDeBytes += ManipuladorDeBit.quantidadeDeBytes(inteiro);//Adicionando a quantidade de Bytes
    }

    //Calculando o novo tamanho do vetor de Quadros
    int novoTamanho = quantidadeDeBytes/3;
    if (quantidadeDeBytes % 3 != 0) {
      novoTamanho++;//Aumentando 1 posicao no tamanho do Vetor
    }

    Quadro[] quadroEnquadrado = new Quadro[novoTamanho];//Novo vetor com os Quadros
    int posQuadro = 0;//Posicao do Vetor de Quadros

    int novoQuadro = 0;//Novo quadro
    Queue<Integer> cargaUtil = new LinkedList<>();//Fila de Carga Util

    //Pesquisando todo o vetor de Inteiros
    for (int i=0; i<quadro.length; i++) {
      int inteiro = quadro[i];
      int bytesInteiro = ManipuladorDeBit.quantidadeDeBytes(inteiro);

      //Pegando todos os Bytes do Inteiro e colocando na Fila de Carga Util
      for (int x=0; x<bytesInteiro; x++) {
        cargaUtil.add(ManipuladorDeBit.getPrimeiroByte(inteiro));
        inteiro <<= 8;
      }

      //Colocando uma quantidade de Caracteres como Carga Util no novo Quadro
      if (cargaUtil.size() == quantidadeDeCaracteres) {
        int header = cargaUtil.size() >= quantidadeDeCaracteres ? quantidadeDeCaracteres*8 : cargaUtil.size()*8;
        
        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,header);
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\tQuadro [ " + header + " ");

        for (int carac=0, tam=cargaUtil.size(); (carac<quantidadeDeCaracteres) && (carac<tam); carac++) {
          int dado = cargaUtil.poll();
          novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,dado);
          Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(dado + " ");
        }

        //ManipuladorDeBit.imprimirBits(novoQuadro);
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("]");
        quadroEnquadrado[posQuadro++] = new Quadro(novoQuadro);//Adicionando o Novo Quadro no Vetor
        Thread.sleep(velocidade);
        
        novoQuadro = 0;//Zerando todos os 32 bits do novoQuadro

      } else if (i == (quadro.length-1)) {
        int header = cargaUtil.size() >= quantidadeDeCaracteres ? quantidadeDeCaracteres*8 : cargaUtil.size()*8;
        
        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,header);
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\tQuadro [ " + header + " ");

        for (int carac=0, tam=cargaUtil.size(); (carac<quantidadeDeCaracteres) && (carac<tam); carac++) {
          int dado = cargaUtil.poll();
          novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,dado);//Adicionando Carga Util
          Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(dado + " ");
        }

        //ManipuladorDeBit.imprimirBits(novoQuadro);
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("]");
        quadroEnquadrado[posQuadro++] = new Quadro(novoQuadro);//Adicionando o Novo Quadro no Vetor
        Thread.sleep(velocidade);
        
        novoQuadro = 0;//Zerando todos os 32 bits do novoQuadro
      }

    }

    return quadroEnquadrado;
  }

  /*********************************************
  * Metodo: enquadramentoInsercaoDeBytes
  * Funcao: Dividir a mensagem em quadros adicionando o byte [S] no inicio do quadro e o byte [E] no final
  * Parametros: quadro : int[]
  * Retorno: quadroEnquadrado : Quadro[]
  *********************************************/
  private Quadro[] enquadramentoInsercaoDeBytes(int[] quadro) throws Exception {
    System.out.println("\n\t[Insercao de Bytes]");
    Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\t[Insercao de Bytes]");
    Thread.sleep(velocidade);

    final char byteFlagStart = 'S';//Identificar o INICIO do quadro (Start)
    final char byteFlagEnd = 'E';//Identificar o FIM do quadro (End)
    final char byteDeEscape = '/';//Caractere de escape especial

    Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\tByte de Inicio de Quadro ["+byteFlagStart+"]");
    Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\tByte de Fim de Quadro ["+byteFlagEnd+"]");
    Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\tByte de Escape de Quadro ["+byteDeEscape+"]\n");
    Thread.sleep(velocidade);


    //Quantidade de Bytes de CargaUtil em cada Quadro = Cada Quadro tem no maximo 32 bits
    final int quantidadeDeCaracteres = 1;//8 de bits de carga util (IC)

    int quantidadeDeBytes = 0;//Armazenar a quantidade Bytes que tem no vetor de Inteiros
    for (int inteiro : quadro) {//Verificando o vetor de Inteiros
      quantidadeDeBytes += ManipuladorDeBit.quantidadeDeBytes(inteiro);//Adicionando a quantidade de Bytes
    }

    //Calculando o novo tamanho do vetor de Quadros
    int novoTamanho = quadro.length;
    Quadro[] quadroEnquadrado = new Quadro[novoTamanho];//Novo Vetor com os Quadros
    int posQuadro = 0;//Posicao do Vetor de Quadros

    int novoQuadro = 0;//Novo Quadro
    Queue<Integer> cargaUtil = new LinkedList<>();//Fila de Carga Util

    //Pesquisando todo o vetor de Inteiros
    for (int i=0; i<quadro.length; i++) {
      int inteiro = quadro[i];
      int bytesInteiro = ManipuladorDeBit.quantidadeDeBytes(inteiro);

      //Pegando todos os Bytes do Inteiro e colocando na Fila de Carga Util
      for (int x=0; x<bytesInteiro; x++) {
        cargaUtil.add(ManipuladorDeBit.getPrimeiroByte(inteiro));
        inteiro <<= 8;
      }

      if (cargaUtil.size() >= quantidadeDeCaracteres) {
        int header = (int) byteFlagStart;//Informacao de Controle (IC) Inicio do Quadro

        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,header);//Adicionando a Informacao de Controle
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\tQuadro [ " + byteFlagStart + " ");

        for (int carac=0, tam=cargaUtil.size(); (carac<quantidadeDeCaracteres) && (carac<tam); carac++) {
          int dado = cargaUtil.poll();

          //VERIFICANDO SE O DADO EH UM DOS BYTES DE CONTROLE
          if (dado == (int) byteFlagStart) {//Caso for o byte de Flag (Start)
            novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro, (int) byteDeEscape);//Adicionando Byte de Escape
            Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(byteDeEscape + " ");
          } else if (dado == (int) byteFlagEnd) {//Caso for o byte de Flag (End)
            novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro, (int) byteDeEscape);//Adicionando Byte de Escape
            Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(byteDeEscape + " ");
          } else if (dado == (int) byteDeEscape) {//Caso for o byte de (Escape)
            novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro, (int) byteDeEscape);//Adicionando Byte de Escape
            Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(byteDeEscape + " ");
          }

          novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,dado);//Adicionando Carga Util
          Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(dado + " ");
        }

        int tail = (int) byteFlagEnd;//Informacao de Controle (IC) Fim do Quadro

        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,tail);//Adicionando a Informacao de Controle
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(byteFlagEnd + " ]");
        Thread.sleep(velocidade);

        quadroEnquadrado[posQuadro++] = new Quadro(novoQuadro);//Adicionando o Novo Quadro no Vetor
        ManipuladorDeBit.imprimirBits(novoQuadro);
        novoQuadro = 0;//Zerando todos os 32 bits do novoQuadro

      } else if (i == (quadro.length-1)) {
        int header = (int) byteFlagStart;//Informacao de Controle (IC) Inicio do Quadro
        
        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,header);//Adicionando a Informacao de Controle
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\tQuadro [ " + byteFlagStart + " ");

        for (int carac=0, tam=cargaUtil.size(); (carac<quantidadeDeCaracteres) && (carac<tam); carac++) {
          int dado = cargaUtil.poll();

          //VERIFICANDO SE O DADO EH UM DOS BYTES DE CONTROLE
          if (dado == (int) byteFlagStart) {//Caso for o byte de Flag (Start)
            novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro, (int) byteDeEscape);//Adicionando Byte de Escape
            Painel.CAMADAS_TRANSMISSORAS.camadaEnlace((int) byteDeEscape + " ");
          } else if (dado == (int) byteFlagEnd) {//Caso for o byte de Flag (End)
            novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro, (int) byteDeEscape);//Adicionando Byte de Escape
            Painel.CAMADAS_TRANSMISSORAS.camadaEnlace((int) byteDeEscape + " ");
          } else if (dado == (int) byteDeEscape) {//Caso for o byte de (Escape)
            novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro, (int) byteDeEscape);//Adicionando Byte de Escape
            Painel.CAMADAS_TRANSMISSORAS.camadaEnlace((int) byteDeEscape + " ");
          }

          novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,dado);//Adicionando Carga Util
          Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(dado + " ");
        }

        int tail = (int) byteFlagEnd;//Informacao de Controle (IC) Fim do Quadro

        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,tail);//Adicionando a Informacao de Controle
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(byteFlagEnd + " ]");
        Thread.sleep(velocidade);

        quadroEnquadrado[posQuadro++] = new Quadro(novoQuadro);//Adicionando o Novo Quadro no Vetor
        ManipuladorDeBit.imprimirBits(novoQuadro);
        novoQuadro = 0;//Zerando todos os 32 bits do novoQuadro
      
      }
    }

    return quadroEnquadrado;
  }

  /*********************************************
  * Metodo: enquadramentoInsercaoDeBits
  * Funcao: Dividir a mensagem em quadros adicionando os bits [01111110] como Informacao de Controle
  * Parametros: quadro : int[]
  * Retorno: quadroEnquadrado : Quadro[]
  *********************************************/
  private Quadro[] enquadramentoInsercaoDeBits(int[] quadro) throws Exception {
    System.out.println("\n\t[Insercao de Bits]\n");
    Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\t[Insercao de Bits]");
    Thread.sleep(velocidade);

    //Byte Flag que contem a sequencia de bits "0111110"
    final int byteFlag = 126;//00000000 00000000 00000000 01111110
    Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\t[Bits de Flag [01111110] = 126]\n\n");
    Thread.sleep(velocidade);

    //Quantidade de Bytes de CargaUtil em cada Quadro = Cada Quadro tem no maximo 32 bits
    final int quantidadeDeCaracteres = 1;//8 de bits de carga util (IC)

    int quantidadeDeBytes = 0;//Armazenar a quantidade Bytes que tem no vetor de Inteiros
    for (int inteiro : quadro) {//Verificando o vetor de Inteiros
      quantidadeDeBytes += ManipuladorDeBit.quantidadeDeBytes(inteiro);//Adicionando a quantidade de Bytes
    }

    //Calculando o novo tamanho do vetor de Quadros
    int novoTamanho = quadro.length;
    Quadro[] quadroEnquadrado = new Quadro[novoTamanho];//Novo Vetor de Quadros
    int posQuadro = 0;//Posicao do Vetor de Quadros

    int novoQuadro = 0;//Novo Quadro
    Queue<Integer> cargaUtil = new LinkedList<>();//Fila de Carga Util


    //Pesquisando todo o vetor de Inteiros
    for (int i=0; i<quadro.length; i++) {
      int inteiro = quadro[i];
      int bytesInteiro = ManipuladorDeBit.quantidadeDeBytes(inteiro);

      //Pegando todos os Bytes do Inteiro e colocando na Fila de Carga Util
      for (int x=0; x<bytesInteiro; x++) {
        cargaUtil.add(ManipuladorDeBit.getPrimeiroByte(inteiro));
        inteiro <<= 8;
      }

      if (cargaUtil.size() >= quantidadeDeCaracteres) {
        int header = byteFlag;//Inforacao de Controle

        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,header);//Adicionando a Informacao de Controle
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\tQuadro [ " + header + " ");

        for (int carac=0, tam=cargaUtil.size(); (carac<quantidadeDeCaracteres) && (carac<tam); carac++) {
          int dado = cargaUtil.poll();

          Boolean cincoBits1 = ManipuladorDeBit.cincoBitsSequenciais(dado,1);

          if (cincoBits1) {
            int auxiliar = ManipuladorDeBit.deslocarBits(dado);
            int novoDado1 = 0;
            int novoDado2 = 0;

            //Cria um inteiro com 1 no bit mais a esquerda e 0s em outros locais
            int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
            //Para cada bit exibe 0 ou 1
            for (int b=1, cont=0; b<=8; b++) {
              //Utiliza displayMask para isolar o bit
              int bit = (auxiliar & displayMask) == 0 ? 0 : 1;

              if (cont == 5) {//Quando encontrar os cinco 1's seguidos
                novoDado1 <<= 1;//Deslocando 1 bit para a esquerda
                novoDado1 |= 0;//Adicionando o bit ao novoDado
                cont = 0;//Zerando o contador
              }

              if (b == 8) {//Caso chegou no ultimo bit
                novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,novoDado1);//Adicionando Carga Util
                Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(novoDado1 + " ");
                novoDado2 =  ManipuladorDeBit.adicionarBitNaPosicao(novoDado2,bit,25);
                novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,novoDado2);//Adicionando Carga Util
                Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(novoDado2 + " ");
              } else {
                novoDado1 <<= 1;//Deslocando 1 bit para a esquerda
                novoDado1 |= bit;//Adicionando o bit ao novoDado
                auxiliar <<= 1;//Desloca o valor uma posicao para a esquerda
              }

              if (bit == 1) {
                cont++;
              }

            }

          } else {//Caso nao tem uma sequencia de 5 Bits 1's
            novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,dado);//Adicionando Carga Util
            Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(dado + " ");
          }
        }

        int tail = byteFlag;//Informacao de Controle

        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,tail);//Adicionando a Informacao de Controle
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(tail + " ]");
        Thread.sleep(velocidade);

        quadroEnquadrado[posQuadro++] = new Quadro(novoQuadro);//Adicionando o Novo Quadro no Vetor
        ManipuladorDeBit.imprimirBits(novoQuadro);
        novoQuadro = 0;//Zerando todos os 32 bits do novoQuadro

      } else if (i == (quadro.length-1)) {
        int header = byteFlag;//Informacao de Controle

        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,header);//Adicionando a Informacao de Controle
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\tQuadro [ " + header + " ");

        for (int carac=0, tam=cargaUtil.size(); (carac<quantidadeDeCaracteres) && (carac<tam); carac++) {
          int dado = cargaUtil.poll();

          Boolean cincoBits1 = ManipuladorDeBit.cincoBitsSequenciais(dado,1);

          if (cincoBits1) {
            int auxiliar = ManipuladorDeBit.deslocarBits(dado);
            int novoDado1 = 0;
            int novoDado2 = 0;

            //Cria um inteiro com 1 no bit mais a esquerda e 0s em outros locais
            int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
            //Para cada bit exibe 0 ou 1
            for (int b=1, cont=0; b<=8; b++) {
              //Utiliza displayMask para isolar o bit
              int bit = (auxiliar & displayMask) == 0 ? 0 : 1;

              if (cont == 5) {//Quando encontrar os cinco 1's seguidos
                novoDado1 <<= 1;//Deslocando 1 bit para a esquerda
                novoDado1 |= 0;//Adicionando o bit ao novoDado
                cont = 0;//Zerando o contador
              }

              if (b == 8) {//Caso chegou no ultimo bit
                novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,novoDado1);//Adicionando Carga Util
                Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(novoDado1 + " ");
                novoDado2 =  ManipuladorDeBit.adicionarBitNaPosicao(novoDado2,bit,25);
                novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,novoDado2);//Adicionando Carga Util
                Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(novoDado2 + " ");
              } else {
                novoDado1 <<= 1;//Deslocando 1 bit para a esquerda
                novoDado1 |= bit;//Adicionando o bit ao novoDado
                auxiliar <<= 1;//Desloca o valor uma posicao para a esquerda
              }

              if (bit == 1) {
                cont++;
              }

            }

          } else {//Caso nao tem uma sequencia de 5 Bits 1's
            novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,dado);//Adicionando Carga Util
            Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(dado + " ");
          }
        }

        int tail = byteFlag;//Informacao de Controle

        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,tail);//Adicionando a Informacao de Controle
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(tail + " ]");
        Thread.sleep(velocidade);

        quadroEnquadrado[posQuadro++] = new Quadro(novoQuadro);//Adicionando o Novo Quadro no Vetor
        ManipuladorDeBit.imprimirBits(novoQuadro);
        novoQuadro = 0;//Zerando todos os 32 bits do novoQuadro
      
      }
    }

    return quadroEnquadrado;
  }

  /*********************************************
  * Metodo: enquadramentoViolacaoCamadaFisica
  * Funcao: Violar a Camada Fisica para que ela determine o inicio e o fim de um quadro
  * Parametros: quadro : int[]
  * Retorno: quadroEnquadrado : Quadro[]
  *********************************************/
  private Quadro[] enquadramentoViolacaoCamadaFisica(int[] quadro) throws Exception {
    System.out.println("\n\t[Violacao da Camada Fisica]");
    Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\t[Violacao da Camada Fisica]");
    Thread.sleep(velocidade);

    final int byteFlag = 255;//00000000 00000000 00000000 11111111
    Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\t[Bits de Flag [1111111] = 255]\n\n");
    Thread.sleep(velocidade);

    //Quantidade de Bytes de CargaUtil em cada Quadro = Cada Quadro tem no maximo 32 bits
    final int quantidadeDeCaracteres = 1;//8 de bits de carga util (IC)

    int quantidadeDeBytes = 0;//Armazenar a quantidade Bytes que tem no vetor de Inteiros
    for (int inteiro : quadro) {//Verificando o vetor de Inteiros
      quantidadeDeBytes += ManipuladorDeBit.quantidadeDeBytes(inteiro);//Adicionando a quantidade de Bytes
    }

    //Calculando o novo tamanho do vetor de Quadros
    int novoTamanho = quadro.length;
    Quadro[] quadroEnquadrado = new Quadro[novoTamanho];//Novo Vetor de Quadros
    int posQuadro = 0;//Posicao do Vetor de Quadros

    int novoQuadro = 0;//Novo Quadro
    Queue<Integer> cargaUtil = new LinkedList<>();//Fila de Carga Util


    //Pesquisando todo o vetor de Inteiros
    for (int i=0; i<quadro.length; i++) {
      int inteiro = quadro[i];
      int bytesInteiro = ManipuladorDeBit.quantidadeDeBytes(inteiro);

      //Pegando todos os Bytes do Inteiro e colocando na Fila de Carga Util
      for (int x=0; x<bytesInteiro; x++) {
        cargaUtil.add(ManipuladorDeBit.getPrimeiroByte(inteiro));
        inteiro <<= 8;
      }

      if (cargaUtil.size() >= quantidadeDeCaracteres) {
        int header = byteFlag;//Inforacao de Controle

        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,header);//Adicionando a Informacao de Controle
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\tQuadro [ " + header + " ");

        for (int carac=0, tam=cargaUtil.size(); (carac<quantidadeDeCaracteres) && (carac<tam); carac++) {
          int dado = cargaUtil.poll();

          Boolean cincoBits1 = ManipuladorDeBit.cincoBitsSequenciais(dado,1);

          if (cincoBits1) {
            int auxiliar = ManipuladorDeBit.deslocarBits(dado);
            int novoDado1 = 0;
            int novoDado2 = 0;

            //Cria um inteiro com 1 no bit mais a esquerda e 0s em outros locais
            int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
            //Para cada bit exibe 0 ou 1
            for (int b=1, cont=0; b<=8; b++) {
              //Utiliza displayMask para isolar o bit
              int bit = (auxiliar & displayMask) == 0 ? 0 : 1;

              if (cont == 5) {//Quando encontrar os cinco 1's seguidos
                novoDado1 <<= 1;//Deslocando 1 bit para a esquerda
                novoDado1 |= 0;//Adicionando o bit ao novoDado
                cont = 0;//Zerando o contador
              }

              if (b == 8) {//Caso chegou no ultimo bit
                novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,novoDado1);//Adicionando Carga Util
                Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(novoDado1 + " ");
                novoDado2 =  ManipuladorDeBit.adicionarBitNaPosicao(novoDado2,bit,25);
                novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,novoDado2);//Adicionando Carga Util
                Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(novoDado2 + " ");
              } else {
                novoDado1 <<= 1;//Deslocando 1 bit para a esquerda
                novoDado1 |= bit;//Adicionando o bit ao novoDado
                auxiliar <<= 1;//Desloca o valor uma posicao para a esquerda
              }

              if (bit == 1) {
                cont++;
              }

            }

          } else {//Caso nao tem uma sequencia de 5 Bits 1's
            novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,dado);//Adicionando Carga Util
            Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(dado + " ");
          }
        }

        int tail = byteFlag;//Informacao de Controle

        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,tail);//Adicionando a Informacao de Controle
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(tail + " ]");
        Thread.sleep(velocidade);

        quadroEnquadrado[posQuadro++] = new Quadro(novoQuadro);//Adicionando o Novo Quadro no Vetor
        ManipuladorDeBit.imprimirBits(novoQuadro);
        novoQuadro = 0;//Zerando todos os 32 bits do novoQuadro

      } else if (i == (quadro.length-1)) {
        int header = byteFlag;//Informacao de Controle

        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,header);//Adicionando a Informacao de Controle
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace("\n\tQuadro [ " + header + " ");

        for (int carac=0, tam=cargaUtil.size(); (carac<quantidadeDeCaracteres) && (carac<tam); carac++) {
          int dado = cargaUtil.poll();

          Boolean cincoBits1 = ManipuladorDeBit.cincoBitsSequenciais(dado,1);

          if (cincoBits1) {
            int auxiliar = ManipuladorDeBit.deslocarBits(dado);
            int novoDado1 = 0;
            int novoDado2 = 0;

            //Cria um inteiro com 1 no bit mais a esquerda e 0s em outros locais
            int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
            //Para cada bit exibe 0 ou 1
            for (int b=1, cont=0; b<=8; b++) {
              //Utiliza displayMask para isolar o bit
              int bit = (auxiliar & displayMask) == 0 ? 0 : 1;

              if (cont == 5) {//Quando encontrar os cinco 1's seguidos
                novoDado1 <<= 1;//Deslocando 1 bit para a esquerda
                novoDado1 |= 0;//Adicionando o bit ao novoDado
                cont = 0;//Zerando o contador
              }

              if (b == 8) {//Caso chegou no ultimo bit
                novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,novoDado1);//Adicionando Carga Util
                Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(novoDado1 + " ");
                novoDado2 =  ManipuladorDeBit.adicionarBitNaPosicao(novoDado2,bit,25);
                novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,novoDado2);//Adicionando Carga Util
                Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(novoDado2 + " ");
              } else {
                novoDado1 <<= 1;//Deslocando 1 bit para a esquerda
                novoDado1 |= bit;//Adicionando o bit ao novoDado
                auxiliar <<= 1;//Desloca o valor uma posicao para a esquerda
              }

              if (bit == 1) {
                cont++;
              }

            }

          } else {//Caso nao tem uma sequencia de 5 Bits 1's
            novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,dado);//Adicionando Carga Util
            Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(dado + " ");
          }
        }

        int tail = byteFlag;//Informacao de Controle

        novoQuadro = ManipuladorDeBit.inserirBits(novoQuadro,tail);//Adicionando a Informacao de Controle
        Painel.CAMADAS_TRANSMISSORAS.camadaEnlace(tail + " ]");
        Thread.sleep(velocidade);

        quadroEnquadrado[posQuadro++] = new Quadro(novoQuadro);//Adicionando o Novo Quadro no Vetor
        ManipuladorDeBit.imprimirBits(novoQuadro);
        novoQuadro = 0;//Zerando todos os 32 bits do novoQuadro
      
      }
    }

    return quadroEnquadrado;
  }


  /***********************************************************************
  * Autor: Cassio Meira Silva
  * Nome: Temporizador
  * Funcao: Aguardar uma confirmacao de recebimento do Receptor; Enviar o Quadro
  ***********************************************************************/
  public class Temporizador extends Thread {
    private int id;//Identificador do Temporizador
    private Quadro quadro;//Dados do Quadro
    private int tempo;//Tempo contador do Temporizador


    /*********************************************
    * Metodo: Temporizador - Construtor
    * Funcao: Criar objetos da Classe Temporizador
    * Parametros: quadro : Quadro
    *********************************************/
    public Temporizador(Quadro quadro) {
      this.id = quadro.getId();
      this.quadro = quadro;
    }

    public void run() {

    }

    /*********************************************
    * Metodo: enviarQuadro
    * Funcao: Envia o Quadro para a Camada Fisica
    * Parametros: void
    * Retorno: quadro : int[]
    *********************************************/
    public int[] enviarQuadro() {
      return this.quadro.getBits();
    }

  }//Fim class Temporizador

  /***********************************************************************
  * Autor: Cassio Meira Silva
  * Nome: Quadro
  * Funcao: Armazenar os dados de um Quadro
  ***********************************************************************/
  public class Quadro {
    private int id;//Identificador do Quadro
    private int[] bits;//Bits do Quadro
    private int tamanhoBits;//Tamanho do Quadro em bits

    /*********************************************
    * Metodo: Temporizador - Construtor
    * Funcao: Criar objetos da Classe Temporizador
    * Parametros: quadro : Quadro
    *********************************************/
    public Quadro(int... bits) {
      this.bits = bits;
      for (int dado : bits) {
        tamanhoBits += ManipuladorDeBit.quantidadeDeBits(dado);
      }
    }

    /*********************************************
    * Metodo: setBits
    * Funcao: Atribui os bits desse Quadro
    * Parametros: bits : int[]
    * Retorno: void
    *********************************************/
    public void setDados(int... bits) {
      this.bits = bits;
      for (int dado : bits) {
        tamanhoBits += ManipuladorDeBit.quantidadeDeBits(dado);
      }
    }

    /*********************************************
    * Metodo: getBits
    * Funcao: Retorna os Bits do Quadro
    * Parametros: void
    * Retorno: bits : int[]
    *********************************************/
    public int[] getBits() {
      return bits;
    }

    /*********************************************
    * Metodo: setId
    * Funcao: Atribuir o Id do Quadro
    * Parametros: id : int
    * Retorno: void
    *********************************************/
    public void setId(int id) {
      this.id = id;
    }

    /*********************************************
    * Metodo: getId
    * Funcao: Retorna os Id do Quadro
    * Parametros: void
    * Retorno: id : int
    *********************************************/
    public int getId() {
      return id;
    }

  }//Fim class Quadro

}//Fim class