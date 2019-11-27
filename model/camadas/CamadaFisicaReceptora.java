/***********************************************************************
* Autor: Cassio Meira Silva
* Matricula: 201610373
* Inicio: 01/02/18
* Ultima alteracao: 17/02/18
* Nome: CamadaFisicaReceptora
* Funcao: Manipular os Bits de um vetor de inteiros e Decodificalo
***********************************************************************/

package model.camadas;

import view.Painel;
import util.ManipuladorDeBit;
import view.componentes.Grafico;

import java.util.concurrent.Semaphore;


public class CamadaFisicaReceptora extends Thread {
  private final String nomeDaCamada = "CAMADA FISICA RECEPTORA";
  private final int velocidade = 300;
  
  //Semaphoro para aguardar o recebimento de todos os Bits
  public static Semaphore semaphoro = new Semaphore(0);

  private int[] fluxoBrutoDeBitsPontoB;

  public void run() {
    System.out.println(nomeDaCamada);
    try {

      Painel.CAMADAS_RECEPTORAS.expandirCamadaFisica();

      Painel.CAMADAS_RECEPTORAS.camadaFisica("Recebendo Bits\n");

      //Bits enviado do Meio de Comunicacao
      int[] fluxoBrutoDeBits = fluxoBrutoDeBitsPontoB;
      //Trava essa Thread de Camadas ate que passa todos os Bits pelo Grafico
      Painel.GRAFICO.semaphoroFim.acquire();
      //Libera a Thread da Camada de Enlace Transmissora para enviar outro Quadro
      CamadaEnlaceDadosTransmissora.SEMAPHORO_QUADRO.release();//USADO PARA INTERFACE


      Painel.CAMADAS_RECEPTORAS.camadaFisica("\n\nBits Brutos [Codificados]\n");
      this.imprimirBits(fluxoBrutoDeBits);

      //Recebendo o tipo de codificacao escolhida pelo Grafico
      Grafico.Codificacao tipoDeCodificacao = Painel.GRAFICO.codificacaoSelecionada();
      switch (tipoDeCodificacao) {
        case CODIFICACAO_BINARIA:
          Painel.CAMADAS_RECEPTORAS.camadaFisica("\n[DECODIFICACAO BINARIA]\n");
          fluxoBrutoDeBits = decodificacaoBinaria(fluxoBrutoDeBits);//DECOFICACAO BINARIA
          break;
        case CODIFICACAO_MANCHESTER:
          Painel.CAMADAS_RECEPTORAS.camadaFisica("\n[DECODIFICACAO MANCHESTER]\n");
          fluxoBrutoDeBits = decodificacaoManchester(fluxoBrutoDeBits);//DECOFICACAO MANCHESTER
          break;
        case CODIFICACAO_MANCHESTER_DIFERENCIAL:
          Painel.CAMADAS_RECEPTORAS.camadaFisica("\n[DECODIFICACAO MANCHESTER DIFERENCIAL]\n");
          fluxoBrutoDeBits = decodificacaoManchesterDiferencial(fluxoBrutoDeBits);//DECOFICACAO MANCHESTER DIFERENCIAL
          break;
      }

      Painel.CAMADAS_RECEPTORAS.camadaFisica("\nBits Brutos [DECODIFICADOS]\n");
      this.imprimirBits(fluxoBrutoDeBits);

      Painel.CAMADAS_RECEPTORAS.camadaFisica("\n");

      int[] quadro = fluxoBrutoDeBits;

      chamarProximaCamada(quadro);
      Painel.GRAFICO.setDisableCodificacao(false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*********************************************
  * Metodo: camadaFisicaReceptora
  * Funcao: Manipular os Bits um vetor de inteiros e codificalo
  * Parametros: quadro : int[]
  * Retorno: void
  *********************************************/
  public void camadaFisicaReceptora(int[] fluxoBrutoDeBitsPontoB) {
    this.fluxoBrutoDeBitsPontoB = fluxoBrutoDeBitsPontoB;
    this.start();//Iniciando a Thread dessa Camada
  }

  /*********************************************
  * Metodo: chamarProximaCamada
  * Funcao: Passa os dados a serem enviados dessa Camada para a proxima
  * Parametros: quadro : int[]
  * Retorno: void
  *********************************************/
  private void chamarProximaCamada(int... quadro) throws Exception {
    new CamadaEnlaceDadosReceptora().camadaEnlaceDadosReceptora(quadro);
  }

  /*********************************************
  * Metodo: imprimirBits
  * Funcao: Imprimir os Bits de cada Inteiros na Interface
  * Parametros: quadro : int[]
  * Retorno: void
  *********************************************/
  public void imprimirBits(int[] quadro) throws Exception {
    for (int b : quadro) {
      Painel.CAMADAS_RECEPTORAS.camadaFisica(ManipuladorDeBit.imprimirBits(b) + "\n");
      Thread.sleep(velocidade);
    }
  }

  /*********************************************
  * Metodo: decodificacaoBinaria
  * Funcao: Retorna um vetor de bits DECODIFICADOS
  * Parametros: quadro : int[]
  * Retorno: int[]
  *********************************************/
  private int[] decodificacaoBinaria(int[] quadro) {
    System.out.println("\tDECODIFICACAO_BINARIA");

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
          System.out.print(" "); //Exibe espaço a cada 8 bits
        }

        //Terminou de adicionar os bits no novo Inteiro
        if (i == numeroDeBits) {
          System.out.print("\n\t\tNovo Inteiro: ");
          ManipuladorDeBit.imprimirBits(novoInteiro);
          vetorCodificado[posicaoQuadro] = novoInteiro;
          System.out.print("\t\t");
        }
      }

      System.out.println();
      posicaoQuadro++;//Passando o proximo Numero
    }//Terminou de percorrer o vetor

    return vetorCodificado;
  }

  /*********************************************
  * Metodo: decodificacaoManchester
  * Funcao: Retorna um vetor de bits DECODIFICADOS
  * Parametros: quadro : int[]
  * Retorno: int[]
  *********************************************/
  private int[] decodificacaoManchester(int[] quadro) {
    System.out.println("\tDECODIFICACAO_MANCHESTER");

    int adicionar = 0;//Numero de Inteiros pra adicionar ao Tamanho 
    int tamanho = quadro.length;//Tamanho do Vetor de Bits

    if (tamanho % 2 != 0) {//Tamanho impar
      adicionar++;//Aumenta uma unidade de tamanho
    }

    //Calculando o novo tamanho do vetor
    int novoTamanho = (tamanho)/2 + adicionar;

    //Vetor que armazena os inteiros com os bits decodificados
    int[] vetorDecodificado = new int[novoTamanho];
    //Cria um inteiro com 1 no bit mais a esquerda e 0s em outros locais
    int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
    int posicaoQuadro = 0;//Indice de posicao do Vetor Quadro
    int posicaoDecodificado = 0;//Indice de posicao do Vetor Codificado

    //Inteiro com todos os bits 0s
    int novoInteiro = 0;//00000000 00000000 00000000 00000000
    int bitsAdicionados = 0;//Bits que foram adicionados dentro do Novo Inteiro

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

      //Percorrendo todos os Bits do Vetor
      for (int i=1; i<=numeroDeBits; i+=2) {
        //Utiliza displayMask para isolar um Bit
        int bit1 = (numero & displayMask) == 0 ? 0 : 1;
        numero <<= 1;//Desloca 1 Bit para a esquerda
        int bit2 = (numero & displayMask) == 0 ? 0 : 1;
        numero <<= 1;//Desloca 1 Bit para a esquerda

        if (bit1 == 1 && bit2 == 0) {//Colocar 1
          novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
          novoInteiro = novoInteiro | 1;//Adicionando o bit [1]
          bitsAdicionados++;
        } else if (bit1 == 0 && bit2 == 1) {//Colocar 0
          novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
          novoInteiro = novoInteiro | 0;//Adicionando o bit [0]
          bitsAdicionados++;
        }
        
        //Caso ja tenha adicionado os 32 bits
        if (bitsAdicionados == 32) {
          vetorDecodificado[posicaoDecodificado] = novoInteiro;//Adicionando no Vetor
          System.out.println("\n\t\tBits Decodificado*********");
          System.out.print("\t\t");
          ManipuladorDeBit.imprimirBits(novoInteiro);
          posicaoDecodificado++;
          bitsAdicionados = 0;//Zerando bits adicionados
          novoInteiro = 0;//Zerando os bits do Inteiro
        }
      }

      System.out.println();
      posicaoQuadro++;//Passando o proximo Numero
    }

    //Caso o novoInteiro nao teve seus 32 bits preenchidos
    if (novoInteiro != 0) {
      vetorDecodificado[posicaoDecodificado] = novoInteiro;//Adicionando no Vetor
      System.out.println("\n\t\tBits Decodificado*********");
      System.out.print("\t\t");
      ManipuladorDeBit.imprimirBits(novoInteiro);
    }

    return vetorDecodificado;
  }

  /*********************************************
  * Metodo: decodificacaoManchesterDiferencial
  * Funcao: Retorna um vetor de bits DECODIFICADOS
  * Parametros: quadro : int[]
  * Retorno: int[]
  *********************************************/
  private int[] decodificacaoManchesterDiferencial(int[] quadro) {
    System.out.println("\tDECODIFICACAO_MANCHESTER_DIFERENCIAL");

    int adicionar = 0;//Numero de Inteiros pra adicionar ao Tamanho 
    int tamanho = quadro.length;//Tamanho do Vetor de Bits

    if (tamanho % 2 != 0) {//Tamanho impar
      adicionar++;//Aumenta uma unidade de tamanho
    }

    //Calculando o novo tamanho do vetor
    int novoTamanho = (tamanho)/2 + adicionar;

    //Vetor que armazena os inteiros com os bits decodificados
    int[] vetorDecodificado = new int[novoTamanho];

    //Cria um valor inteiro com 1 no bit mais a esquerda e 0s em outros locais
    int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
    int posicaoQuadro = 0;//Indice de posicao do Vetor Quadro
    int posicaoDecodificado = 0;//Indice de posicao do Vetor Codificado

    //Iniciando os niveis de Sinal como [ALTO | BAIXO] = 0
    boolean sinal1 = true;//Sinal definido como ALTO
    boolean sinal2 = false;//Sinal definido como BAIXO

    //Inteiro com todos os bits 0s
    int novoInteiro = 0;//00000000 00000000 00000000 00000000

    int bitsAdicionados = 0;//Bits que foram adicionados dentro do Novo Inteiro

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

      //Percorrendo todos os Bits do Vetor
      for (int i=1; i<=numeroDeBits; i+=2) {
        //Utiliza displayMask para isolar um Bit
        boolean bit1 = (numero & displayMask) == 0 ? false : true;
        numero <<= 1;//Desloca 1 Bit para a esquerda
        boolean bit2 = (numero & displayMask) == 0 ? false : true;
        numero <<= 1;//Desloca 1 Bit para a esquerda

        //Se o Par recebido for IGUAL ao anterior, significa que este e 0
        if (bit1 == sinal1 && bit2 == sinal2) {
          novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
          novoInteiro = novoInteiro | 0;//Adicionando o bit [0]
          bitsAdicionados++;
        } else if (bit1 != sinal1 && bit2 != sinal2) {//Se o Par recebido for DIFERENTE ao anterior, significa que este e 1
          novoInteiro <<= 1;//Desloca 1 Bit para a esquerda
          novoInteiro = novoInteiro | 1;//Adicionando o bit [1]
          bitsAdicionados++;
        }

        sinal1 = bit1;//Atualizando Sinais para os proximos
        sinal2 = bit2;//Atualizando Sinais para os proximos

        //Caso ja tenha adicionado os 32 bits
        if (bitsAdicionados == 32) {
          vetorDecodificado[posicaoDecodificado] = novoInteiro;//Adicionando no Vetor
          System.out.println("\n\t\tBits Decodificado*********");
          System.out.print("\t\t");
          ManipuladorDeBit.imprimirBits(novoInteiro);
          posicaoDecodificado++;
          bitsAdicionados = 0;
          novoInteiro = 0;//Zerando os bits do Inteiro
        }
      }

      System.out.println();
      posicaoQuadro++;//Passando o proximo Numero
    }

    if (novoInteiro != 0) {
      vetorDecodificado[posicaoDecodificado] = novoInteiro;//Adicionando no Vetor
      System.out.println("\n\t\tBits Decodificado*********");
      System.out.print("\t\t");
      ManipuladorDeBit.imprimirBits(novoInteiro);
    }

    return vetorDecodificado;
  }

}//Fim class