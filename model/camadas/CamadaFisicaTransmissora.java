/***********************************************************************
* Autor: Cassio Meira Silva
* Matricula: 201610373
* Inicio: 01/02/18
* Ultima alteracao: 17/02/18
* Nome: CamadaFisicaTransmissora
* Funcao: Manipular os Bits de um vetor de inteiros e Codificalo
***********************************************************************/

package model.camadas;

import view.Painel;
import view.componentes.Grafico;
import view.componentes.Grafico.Codificacao;
import util.ManipuladorDeBit;


public class CamadaFisicaTransmissora extends Thread {
  private final String nomeDaCamada = "CAMADA FISICA TRANSMISSORA";
  private final int velocidade = 300;

  public static boolean VIOLADA = false;//Muda se a Camada Fisica foi violada

  private int[] quadro;

  public void run() {
    System.out.println(nomeDaCamada);
    try {
      Painel.GRAFICO.setDisableCodificacao(true);
      Painel.CAMADAS_TRANSMISSORAS.expandirCamadaFisica();
      Thread.sleep(velocidade);

      Painel.CAMADAS_TRANSMISSORAS.camadaFisica("Bits Brutos [Decodificados]\n");
      this.imprimirBits(quadro);

      int[] fluxoBrutoDeBits = quadro;


      //Recebendo o tipo de codificacao escolhida pelo Grafico
      Grafico.Codificacao tipoDeCodificacao = Painel.GRAFICO.codificacaoSelecionada();

      //VERIFICANDO SE A CAMADA FISICA FOI VIOLADA
      if (verificarViolacaoDaCamadaFisica(fluxoBrutoDeBits)) {
        System.out.println("CAMADA FISICA VIOLADA");
        VIOLADA = true;

        //CASO TIVER SELECIONADA A CODIFICACAO BINARIA, MUDAR PARA A MANCHESTER
        if (tipoDeCodificacao == Codificacao.CODIFICACAO_BINARIA) {
          tipoDeCodificacao = Codificacao.CODIFICACAO_MANCHESTER;
          Painel.GRAFICO.setCodificacao(Codificacao.CODIFICACAO_MANCHESTER);
        }

      }

      
      switch (tipoDeCodificacao) {
        case CODIFICACAO_BINARIA:
          fluxoBrutoDeBits = codificacaoBinaria(fluxoBrutoDeBits);//CODIFICACAO BINARIA
          break;
        case CODIFICACAO_MANCHESTER:
          fluxoBrutoDeBits = codificacaoManchester(fluxoBrutoDeBits);//CODIFICACAO MANCHESTER
          break;
        case CODIFICACAO_MANCHESTER_DIFERENCIAL:
          fluxoBrutoDeBits = codificacaoManchesterDiferencial(fluxoBrutoDeBits);//CODIFICACAO MANCHESTER DIFERENCIAL
          break;
      }

      

      Thread.sleep(velocidade);

      Painel.CAMADAS_TRANSMISSORAS.camadaFisica("\n\tBits Brutos [CODIFICADOS]\n");
      this.imprimirBits(fluxoBrutoDeBits);

      chamarProximaCamada(fluxoBrutoDeBits);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*********************************************
  * Metodo: camadaFisicaTransmissora
  * Funcao: Manipular os Bits um vetor de inteiros e codificalo
  * Parametros: quadro : int[]
  * Retorno: void
  *********************************************/
  public void camadaFisicaTransmissora(int... quadro) {
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
    MeioDeComunicacao.meioDeComunicacao(quadro);
  }

  /*********************************************
  * Metodo: imprimirBits
  * Funcao: Imprimir os Bits de cada Inteiros na Interface
  * Parametros: quadro : int[]
  * Retorno: void
  *********************************************/
  public void imprimirBits(int... quadro) throws Exception {
    for (int b : quadro) {
      Painel.CAMADAS_TRANSMISSORAS.camadaFisica(ManipuladorDeBit.imprimirBits(b) + "\n");
      Thread.sleep(velocidade);
    }
  }

  /*********************************************
  * Metodo: codificacaoBinaria
  * Funcao: Retorna um vetor de bits na CODIFICACAO Binaria
  * Parametros: quadro : int[]
  * Retorno: int[]
  *********************************************/
  private int[] codificacaoBinaria(int[] quadro) {
    Painel.CAMADAS_TRANSMISSORAS.camadaFisica("\n[CODIFICACAO BINARIA]\n");

    int[] vetorCodificado = new int[quadro.length];

    //Cria um inteiro com 1 no bit mais a esquerda e 0s em outros locais
    int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
    int posicaoQuadro = 0;//Indice de posicao do Vetor Quadro
    int posicaoCodificado = 0;//Indice de posicao do Vetor Codificado

    //Percorrendo todo o Vetor de Inteiros para pegar os respectivos Bits
    while (posicaoQuadro < quadro.length) {
      int numero = quadro[posicaoQuadro];//Numero do qual sera copiado os Bits
      
      int numeroDeBits = Integer.toBinaryString(numero).length();//Quantidade de Bits que o inteiro possui
      System.out.println("\t\tNumero de Bits " + numeroDeBits);

      if (numeroDeBits <= 8) {        //Arredondando o numero de Bits para 8
        numeroDeBits = 8;
      } else if (numeroDeBits <= 16) {//Arredondando o numero de Bits para 16
        numeroDeBits = 16;
      } else if (numeroDeBits <= 24) {//Arredondando o numero de Bits para 24
        numeroDeBits = 24;
      } else if (numeroDeBits <= 32) {//Arredondando o numero de Bits para 32
        numeroDeBits = 32;
      }

      System.out.println("\t\tNumero Arredondando " + numeroDeBits);
      System.out.println("\t\tDeslocar " + (32-numeroDeBits) + " bits a esquerda");

      numero <<= (32-numeroDeBits);//Deslocando um valor de Bits para a esquerda
      System.out.println("\t\tBits do numero: ");
      System.out.print("\t\t");
      ManipuladorDeBit.imprimirBits(numero);

      System.out.println("\n\t\tBit a Bit do Numero");
      System.out.print("\t\t");

      //Inteiro com todos os bits 0s
      int novoInteiro = 0;//00000000 00000000 00000000 00000000

      //Percorrendo todos os Bits do Vetor
      for (int i=1; i<=numeroDeBits; i++) {
        //Utiliza displayMask para isolar um Bit
        int bit = (numero & displayMask) == 0 ? 0 : 1;
        System.out.print(bit);

        novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
        novoInteiro = novoInteiro | bit;//Adicionando novo Bit ao Inteiro
        numero <<= 1;//Desloca 1 Bit para a esquerda

        if (i % 8 == 0) {
          System.out.print(" ");//Exibe espaço a cada 8 bits
        }

        //Terminou de adicionar os bits no novo Inteiro
        if (i == numeroDeBits) {
          System.out.print("\n\t\tNovo Inteiro: ");
          //imprimirBits(novoInteiro);
          ManipuladorDeBit.imprimirBits(novoInteiro);
          vetorCodificado[posicaoQuadro] = novoInteiro;//Adicionando no vetor
          System.out.print("\t\t");
        }
      }

      System.out.println();
      posicaoQuadro++;//Passando o proximo Numero
    }//Terminou de percorrer o vetor

    return vetorCodificado;
  }

  /*********************************************
  * Metodo: codificacaoManchester
  * Funcao: Retorna um vetor de bits na CODIFICACAO Manchester
  * Parametros: quadro : int[]
  * Retorno: int[]
  *********************************************/
  private int[] codificacaoManchester(int[] quadro) {
    Painel.CAMADAS_TRANSMISSORAS.camadaFisica("\n[CODIFICACAO MANCHESTER]\n");

    int reduzir = 0;//Numero de Inteiros pra reduzir ao Tamanho
    int tamanho = quadro.length;//Tamanho do Vetor de Bits
    //Numero de Bits que o ultimo Inteiro de vetor possui
    int numeroDeBitsUltimoInteiro = Integer.toBinaryString(quadro[quadro.length - 1]).length();
    
    if (numeroDeBitsUltimoInteiro <= 16) {
      reduzir = 1;
    }

    //Calculando o novo tamanho do vetor
    int novoTamanho = (quadro.length*2) - reduzir;

    //Vetor que armazena os inteiros com os bits codificados
    int[] vetorCodificado = new int[novoTamanho];

    //Cria um valor inteiro com 1 no bit mais a esquerda e 0s em outros locais
    int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
    int posicaoQuadro = 0;//Indice de posicao do Vetor Quadro
    int posicaoCodificado = 0;//Indice de posicao do Vetor Codificado

    //Inteiro com todos os bits 0s
    int novoInteiro = 0;//00000000 00000000 00000000 00000000

    //Percorrendo todo o Vetor de Inteiros para pegar os respectivos Bits
    while (posicaoQuadro < quadro.length) {
      int numero = quadro[posicaoQuadro];//Numero do qual sera copiado os Bits
      
      int numeroDeBits = Integer.toBinaryString(numero).length();//Quantidade de Bits que o inteiro possui
      System.out.println("\t\tNumero de Bits " + numeroDeBits);

      if (numeroDeBits <= 8) {        //Arredondando o numero de Bits para 8
        numeroDeBits = 8;
      } else if (numeroDeBits <= 16) {//Arredondando o numero de Bits para 16
        numeroDeBits = 16;
      } else if (numeroDeBits <= 24) {//Arredondando o numero de Bits para 24
        numeroDeBits = 24;
      } else if (numeroDeBits <= 32) {//Arredondando o numero de Bits para 32
        numeroDeBits = 32;
      }

      System.out.println("\t\tNumero Arredondando " + numeroDeBits);
      System.out.println("\t\tDeslocar " + (32-numeroDeBits) + " bits a esquerda");

      numero <<= (32-numeroDeBits);//Deslocando um valor de Bits para a esquerda
      System.out.println("\t\tBits do numero: ");
      System.out.print("\t\t");
      //imprimirBits(numero);
      ManipuladorDeBit.imprimirBits(numero);

      System.out.println("\n\t\tBit a Bit do Numero");
      System.out.print("\t\t");

      //Percorrendo todos os Bits do Vetor
      for (int i=1; i<=numeroDeBits; i++) {
        //Utiliza displayMask para isolar um Bit
        int bit = (numero & displayMask) == 0 ? 0 : 1;

        if (bit == 1) {//Colocar 10
          System.out.print("10");
          novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
          novoInteiro = novoInteiro | 1;//Adicionando o bit [1]
          novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
          novoInteiro = novoInteiro | 0;//Adicionando o bit [0]
        } else if (bit == 0) {//Colocar 01
          System.out.print("01");
          novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
          novoInteiro = novoInteiro | 0;//Adicionando o bit [0]
          novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
          novoInteiro = novoInteiro | 1;//Adicionando o bit [1]
        }

        numero <<= 1;//Desloca 1 Bit para a esquerda

        if (i % 4 == 0) {
          System.out.print(" ");
        }

        if (i == 16) {//Verificando se percorreu a metade dos bits do Inteiro
          System.out.print("\n\t\tNovo Inteiro: ");
          //imprimirBits(novoInteiro);
          ManipuladorDeBit.imprimirBits(novoInteiro);
          vetorCodificado[posicaoCodificado] = novoInteiro;//Adicionando no Vetor
          novoInteiro = 0;//Zerando os bits do Inteiro
          posicaoCodificado++;
          System.out.println("\t\tBit a Bit do Numero");
          System.out.print("\t\t");
        
        } else if (i == numeroDeBits) {//Terminou de adicionar os bits no novo Inteiro
          System.out.print("\n\t\tNovo Inteiro: ");
          //imprimirBits(novoInteiro);
          ManipuladorDeBit.imprimirBits(novoInteiro);
          vetorCodificado[posicaoCodificado] = novoInteiro;//Adicionando no Vetor
          novoInteiro = 0;//Zerando os bits do Inteiro
          posicaoCodificado++;
          System.out.print("\t\t");
        }
      }

      System.out.println();
      posicaoQuadro++;//Passando o proximo Numero
    }

    return vetorCodificado;
  }

  /*********************************************
  * Metodo: codificacaoManchesterDiferencial
  * Funcao: Retorna um vetor de bits na CODIFICACAO Manchester Diferencial
  * Parametros: quadro : int[]
  * Retorno: int[]
  *********************************************/
  private int[] codificacaoManchesterDiferencial(int[] quadro) {
    Painel.CAMADAS_TRANSMISSORAS.camadaFisica("\n[CODIFICACAO MANCHESTER DIFERENCIAL]\n");

    int reduzir = 0;//Numero de Inteiros pra reduzir ao Tamanho
    int tamanho = quadro.length;//Tamanho do Vetor de Bits
    //Numero de Bits que o ultimo Inteiro de vetor possui
    int numeroDeBitsUltimoInteiro = Integer.toBinaryString(quadro[quadro.length - 1]).length();
    
    if (numeroDeBitsUltimoInteiro <= 16) {
      reduzir = 1;
    }

    //Calculando o novo tamanho do vetor
    int novoTamanho = (quadro.length*2) - reduzir;

    //Vetor que armazena os inteiros com os bits codificados
    int[] vetorCodificado = new int[novoTamanho];

    //Cria um inteiro com 1 no bit mais a esquerda e 0s em outros locais
    int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
    int posicaoQuadro = 0;//Indice de posicao do Vetor Quadro
    int posicaoCodificado = 0;//Indice de posicao do Vetor Codificado

    //Iniciando os niveis de Sinal como [ALTO | BAIXO] = 0
    boolean sinal1 = true;//Sinal definido como ALTO
    boolean sinal2 = false;//Sinal definido como BAIXO

    //Inteiro com todos os bits 0s
    int novoInteiro = 0;//00000000 00000000 00000000 00000000

    //Percorrendo todo o Vetor de Inteiros para pegar os respectivos Bits
    while (posicaoQuadro < quadro.length) {
      int numero = quadro[posicaoQuadro];//Numero do qual sera copiado os Bits
      
      int numeroDeBits = Integer.toBinaryString(numero).length();//Quantidade de Bits que o inteiro possui
      System.out.println("\t\tNumero de Bits " + numeroDeBits);

      if (numeroDeBits <= 8) {        //Arredondando o numero de Bits para 8
        numeroDeBits = 8;
      } else if (numeroDeBits <= 16) {//Arredondando o numero de Bits para 16
        numeroDeBits = 16;
      } else if (numeroDeBits <= 24) {//Arredondando o numero de Bits para 24
        numeroDeBits = 24;
      } else if (numeroDeBits <= 32) {//Arredondando o numero de Bits para 32
        numeroDeBits = 32;
      }

      System.out.println("\t\tNumero Arredondando " + numeroDeBits);
      System.out.println("\t\tDeslocar " + (32-numeroDeBits) + " bits a esquerda");

      numero <<= (32-numeroDeBits);//Deslocando um valor de Bits para a esquerda
      System.out.println("\t\tBits do numero: ");
      System.out.print("\t\t");
      //imprimirBits(numero);
      ManipuladorDeBit.imprimirBits(numero);

      System.out.println("\n\t\tBit a Bit do Numero");
      System.out.print("\t\t");

      //Percorrendo todos os Bits do Vetor
      for (int i=1; i<=numeroDeBits; i++) {
        //Utiliza displayMask para isolar um Bit
        int bit = (numero & displayMask) == 0 ? 0 : 1;

        if (bit == 0) {//BIT 0 INVERTE O NIVEL DE SINAL
          sinal1 = sinal1;//Invertendo o sinal do Nivel Anterior
          sinal2 = sinal2;//Invertendo o sinal do Nivel Anterior
        } else if (bit == 1) {//BIT 1 MANTEM O NIVEL DE SINAL
          sinal1 = !sinal1;//Mantendo o sinal do Nivel Anterior
          sinal2 = !sinal2;//Mentando o sinal do Nivel Anterior
        }

        //VERIFICANDO OS NIVEIS DE SINAIS
        if (sinal1 && !sinal2) {//Colocar 10
          novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
          novoInteiro = novoInteiro | 1;//Adicionando o bit [1]
          novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
          novoInteiro = novoInteiro | 0;//Adicionando o bit [0]
        } else if (!sinal1 && sinal2) {//Colocar 01
          novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
          novoInteiro = novoInteiro | 0;//Adicionando o bit [0]
          novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
          novoInteiro = novoInteiro | 1;//Adicionando o bit [1]
        }

        numero <<= 1;//Desloca 1 Bit para a esquerda

        if (i == 16) {//Verificando se percorreu a metade dos bits do Inteiro
          System.out.print("\n\t\tNovo Inteiro: ");
          //imprimirBits(novoInteiro);
          ManipuladorDeBit.imprimirBits(novoInteiro);
          vetorCodificado[posicaoCodificado] = novoInteiro;//Adicionando no Vetor
          novoInteiro = 0;//Zerando os bits do Inteiro
          posicaoCodificado++;
          System.out.println("\t\tBit a Bit do Numero");
          System.out.print("\t\t");
        
        } else if (i == numeroDeBits) {//Terminou de adicionar os bits no novo Inteiro
          System.out.print("\n\t\tNovo Inteiro: ");
          //imprimirBits(novoInteiro);
          ManipuladorDeBit.imprimirBits(novoInteiro);
          vetorCodificado[posicaoCodificado] = novoInteiro;//Adicionando no Vetor
          novoInteiro = 0;//Zerando os bits do Inteiro
          posicaoCodificado++;
          System.out.print("\t\t");
        }
      }

      System.out.println();
      posicaoQuadro++;//Passando o proximo Numero
    }

    return vetorCodificado;
  }

  /*********************************************
  * Metodo: verificarViolacaoDaCamadaFisica
  * Funcao: Verifica se o Quadro recebido contem a informacao de VIOLACAO
  * Parametros: quadro : int[]
  * Retorno: boolean
  *********************************************/
  private boolean verificarViolacaoDaCamadaFisica(int[] quadro) {
    final int byteDeViolacao = 255;//00000000 00000000 00000000 11111111

    int primeiroInteiro = quadro[0];
    int primeiroByte = ManipuladorDeBit.getPrimeiroByte(primeiroInteiro);

    //Verificando se a Camada Fisica foi violada
    if (primeiroByte == byteDeViolacao) {
      return true;
    }

    return false;
  }

}//Fim class