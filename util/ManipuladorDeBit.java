/***********************************************************************
* Autor: Cassio Meira Silva
* Matricula: 201610373
* Inicio: 09/03/18
* Ultima alteracao: --/--/--
* Nome: ManipuladorDeBit
* Funcao: 
***********************************************************************/

package util;


public class ManipuladorDeBit {

  /*********************************************
  * Metodo: imprimirBits
  * Funcao: Imprime na tela os Bits de um numero Inteiro
  * Parametros: numero : int
  * Retorno: void
  *********************************************/
  public static String imprimirBits(int numero) {
    String bits = "";
    //Cria um inteiro com 1 no bit mais a esquerda e 0s em outros locais
    int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
    //Para cada bit exibe 0 ou 1
    for (int bit=1; bit<=32; bit++) {
      //Utiliza displayMask para isolar o bit
      System.out.print((numero & displayMask) == 0 ? '0' : '1');
      bits += (numero & displayMask) == 0 ? '0' : '1';
      numero <<= 1;//Desloca o valor uma posicao para a esquerda
      if ( bit % 8 == 0 ) {
        System.out.print(" ");//Exibe espaco a cada 8 bits
        bits += " ";
      }
    }
    System.out.println();
    return bits;
  }

  /*********************************************
  * Metodo: inteirosParaBits
  * Funcao: Manipular um vetor de inteiros para um vetor de inteiros com os Bits manipulados
  * Parametros: vetorDeInteiros : int[]
  * Retorno: int[] : vetor com Bits manipulados
  *********************************************/
  public static int[] inteirosParaBits(int[] vetorDeInteiros) {
    //Definindo um novo tamanho para o Vetor com de bits manipulados
    //O novo tamanho do vetor sera o inteiro da divisao por 4
    //Pois em cada inteiro eh possivel colocar 4 bytes de 8 bits, ou seja, 4 numeros
    int novoTamanho = vetorDeInteiros.length/4;
    //Caso o Vetor tenha MAIS de 4 numeros o resto da divisao sera diferente de 0
    //E sera necessario adicionar mais uma posicao no tamanho do Vetor
    if (vetorDeInteiros.length % 4 != 0) {
      novoTamanho++;//Aumentando 1 posicao no tamanho do Vetor
    }
    
    //Vetor que armazena os Bits manipulados
    int[] vetorDeBits = new int[novoTamanho];//Vetor com os bits
      
    //int com todos os 32 bits 0s
    int novoBit = 0;//00000000 00000000 00000000 00000000
    int posicaoV = 0;//Indice de posicao do Vetor de INTEIROS
    int posicaoR = 0;//Indice de posicao do Vetor de BITS
    
    //Percorrendo todo o Vetor de Inteiros para pegar os respectivos Bits
    while (posicaoV < vetorDeInteiros.length) {
      novoBit <<= 8;//Deslocando 8 bits para a Esquerda
      novoBit = novoBit | vetorDeInteiros[posicaoV];//Adicionando os bits do Vetor ao novoBit
      
      //Verificando se ja adicionou os 32 bits no Inteiro
      if ((posicaoV+1) % 4 == 0) {
        vetorDeBits[posicaoR] = novoBit;//Adicionando o Inteiro com 32 bits no Vetor de Bits
        novoBit = 0;//Limpando novoBit para colocar os bits do outros numeros
        posicaoR++;//Aumentando o indice do Vetor Resultado em 1 posicao
      }

      posicaoV++;//Passando para o proximo Inteiro
    }//Fim do While

    //Caso o novoBit nao tenha preenchido seus 32 bits sera diferente de zero
    if (novoBit != 0) {
      vetorDeBits[posicaoR] = novoBit;//Adicionando o Inteiro com os bits no Vetor de Bits
    }

    return vetorDeBits;
  }

  /*********************************************
  * Metodo: bitsParaInteiros
  * Funcao: Converter um vetor de inteiros com os Bits Manipulados para um vetor de inteiros
  * Parametros: vetorDeBits : int[]
  * Retorno: int[] vetor de Inteiros
  *********************************************/
  public static int[] bitsParaInteiros(int[] vetorDeBits) {
    int adicionar = 0;//Numero de Inteiros pra adicionar ao Tamanho 
    int reduzir = 0;//Numero de Inteiros pra reduzir ao Tamanho
    int tamanho = vetorDeBits.length;//Tamanho do Vetor de Bits
    //Numero de Bits que o ultimo Inteiro do vetor possui
    int numeroDeBitsUltimoInteiro = Integer.toBinaryString(vetorDeBits[vetorDeBits.length - 1]).length();
    
    //Caso o ultimo numero do Vetor de Bits tiver menos de 32 bits
    //Descobrir quantos Bits tem nesse Interos
    if (numeroDeBitsUltimoInteiro <= 24) {
      
      if (numeroDeBitsUltimoInteiro <= 8) {//Caso tenha 8 bits ou 1 NUMERO
        adicionar += 1;//Adiciona 1 numero ao NOVO TAMANHO
      } else if (numeroDeBitsUltimoInteiro <= 16) {//Caso tenha 16 bits ou 2 NUMEROS
        adicionar += 2;//Adiciona 2 numeros ao NOVO TAMANHO
      } else if (numeroDeBitsUltimoInteiro <= 24) {//Caso tenha 24 bits ou 3 NUMEROS
        adicionar += 3;//Adiciona 3 numeros ao NOVO TAMANHO
      }

      reduzir = 1;//Reduz 1 unidade de tamanho na multiplicacao
    }

    //Calculando NOVO TAMANHO DO VETOR DE INTEIROS
    int novoTamanho = ((tamanho-reduzir) * 4) + adicionar;
    
    //Vetor que armazenas os Inteiros
    int[] vetorDeInteiros = new int[novoTamanho];

    //cria um valor inteiro com 1 no bit mais à esquerda e 0s em outros locais
    int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
    int posicaoI = 0;//Indice de posicao de Vetor de Inteiros

    //Percorrendo todo o Vetor de Inteiros com Bits manipulados
    for (int intBits : vetorDeBits) {
      //Inteiro com todos os bits 0s
      int novoInteiro = 0;//00000000 00000000 00000000 00000000
      
      //Percorrendo os 32 bits do Inteiro
      for (int i = 1; i <= 32; i++) {
        // utiliza displayMask para isolar o bit
        int bit = (intBits & displayMask) == 0 ? 0 : 1;
        novoInteiro <<= 1;//Deslocando 1 bit para a esquerda
        novoInteiro = novoInteiro | bit;//Adicionando novo Bit ao Inteiro
        intBits <<= 1;//Deslocando 1 bit para a esquerda

        //Quando completar os 8 bits de um Inteiro
        if (i%8 == 0 && novoInteiro != 0) {
          vetorDeInteiros[posicaoI] = novoInteiro;//Adicionando no Vetor de Inteiros
          posicaoI++;//Aumentando 1 posicao no Vetor de Inteiros
          novoInteiro = 0;//Zerando bits para ser um novo Inteiro
        }
      }//Terminou de percorrer os 32 bits

    }//Terminou de percorrer o vetor

    return vetorDeInteiros;
  }

  /*********************************************
  * Metodo: 
  * Funcao: 
  * Parametros: 
  * Retorno: 
  *********************************************/
  public static int inserirBits(int numero, int bitsParaInserir) {
    int quantidadeDeBitsNumero = quantidadeDeBits(numero);
    int quantidadeDeBitsDeslocar = quantidadeDeBits(bitsParaInserir);

    //imprimirBits(numero);
    //imprimirBits(bitsParaInserir);

    if ((quantidadeDeBitsNumero + quantidadeDeBitsDeslocar) > 32) {
      System.out.println("[Erro] ao tentar Inserir Bits");
      return numero;
    }

    numero <<= quantidadeDeBitsDeslocar;
    numero = numero | bitsParaInserir;
    //imprimirBits(numero);

    return numero;
  }

  /*********************************************
  * Metodo: 
  * Funcao: 
  * Parametros: 
  * Retorno: 
  *********************************************/
  public static int getPrimeiroByte(int numero) {
    int primeiroByte = 0;
    numero = deslocarBits(numero);

    int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
    for (int i=1; i<=8; i++) {
      int bit = (numero & displayMask) == 0 ? 0 : 1;
      primeiroByte <<= 1;//Deslocando 1 bit para a esquerda
      primeiroByte = primeiroByte | bit;//Adicionando novo Bit ao Inteiro
      numero <<= 1;//Deslocando 1 bit para a esquerda
    }

    return primeiroByte;
  }

  /*********************************************
  * Metodo: 
  * Funcao: 
  * Parametros: 
  * Retorno: 
  *********************************************/
  public static int deslocarBits(int numero) {
    numero <<= (32-quantidadeDeBits(numero));//Deslocando um valor de Bits para a esquerda
    return numero;
  }

  /*********************************************
  * Metodo: 
  * Funcao: 
  * Parametros: 
  * Retorno: 
  *********************************************/
  public static int quantidadeDeBits(int numero) {
    //Quantidade de Bits que o inteiro possui
    int numeroDeBits = Integer.toBinaryString(numero).length();
    if (numeroDeBits <= 8) {        //Arredondando o numero de Bits para 8
      numeroDeBits = 8;
    } else if (numeroDeBits <= 16) {//Arredondando o numero de Bits para 16
      numeroDeBits = 16;
    } else if (numeroDeBits <= 24) {//Arredondando o numero de Bits para 24
      numeroDeBits = 24;
    } else if (numeroDeBits <= 32) {//Arredondando o numero de Bits para 32
      numeroDeBits = 32;
    }
    return numeroDeBits;
  }

  /*********************************************
  * Metodo: 
  * Funcao: 
  * Parametros: 
  * Retorno: 
  *********************************************/
  public static int quantidadeDeBytes(int numero) {
    //Quantidade de Bits que o inteiro possui
    int numeroDeBits = Integer.toBinaryString(numero).length();
    int numeroDeBytes = 0;
    if (numeroDeBits <= 8) {        //Arredondando o numero de Bits para 8
      numeroDeBytes = 1;
    } else if (numeroDeBits <= 16) {//Arredondando o numero de Bits para 16
      numeroDeBytes = 2;
    } else if (numeroDeBits <= 24) {//Arredondando o numero de Bits para 24
      numeroDeBytes = 3;
    } else if (numeroDeBits <= 32) {//Arredondando o numero de Bits para 32
      numeroDeBytes = 4;
    }
    return numeroDeBytes;
  }

  /*********************************************
  * Metodo: 
  * Funcao: 
  * Parametros: 
  * Retorno: 
  *********************************************/
  public static Boolean cincoBitsSequenciais(int numero, int bit) {
    numero = ManipuladorDeBit.deslocarBits(numero);//Deslocando os bits para a esquerda
    int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
    Boolean bitsSequenciais = false;
    //VERIFICANDO SE O DADO POSSUI UMA SEQUENCIA DE 5 BITS "1"
    for (int cont=1, contador=0; cont<=8; cont++) {
      int inteiroBit = (numero & displayMask) == 0 ? 0 : 1;
      //Verificando o bit recebido
      if (inteiroBit == bit) {
        contador++;//Adicionando 1 ao contador
      } else {
        contador = 0;
      }
      //Caso tiver encontrado uma sequencia de 5 bits 1's
      if (contador == 5) {
        bitsSequenciais = true;
      }
      numero <<= 1;//Deslocando 1 bit para a esquerda
    }
    return bitsSequenciais;    
  }

  /*********************************************
  * Metodo: 
  * Funcao: 
  * Parametros: 
  * Retorno: 
  *********************************************/
  public static int inverterBitNaPosicao(int numero, int posicao) {
    System.out.print("Antes:  ");
    imprimirBits(numero);

    int novoNumero = 0;

    //VERIFICANDO SE A POSICAO PRA ALTERAR ESTA CORRETA
    if (posicao > 0 && posicao <= 32) {
      int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
      //Para cada bit exibe 0 ou 1
      for (int i=1; i<=32; i++) {
        //Utiliza displayMask para isolar o bit
        int bit = (numero & displayMask) == 0 ? 0 : 1;
        novoNumero <<= 1;//Desloca um bit para a esquerda
        if (i == posicao) {
          novoNumero |= (bit == 1) ? 0 : 1;
        } else {
          novoNumero |= bit;
        }
        numero <<= 1;//Desloca um bit para a esquerda
      }

      System.out.print("\nDepois: ");
      imprimirBits(novoNumero);
    } else {
      System.out.println("Erro: a poiscao [" + posicao + "] nao eh valida");
    }

   return novoNumero;
  }

  /*********************************************
  * Metodo: 
  * Funcao: 
  * Parametros: 
  * Retorno: 
  *********************************************/
  public static int adicionarBitNaPosicao(int numero, int adBit, int posicao) {
    int novoNumero = 0;
    //VERIFICANDO SE A POSICAO PRA ALTERAR ESTA CORRETA
    if (posicao > 0 && posicao <= 32) {
      int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
      //Para cada bit exibe 0 ou 1
      for (int i=1; i<=32; i++) {
        //Utiliza displayMask para isolar o bit
        int bit = (numero & displayMask) == 0 ? 0 : 1;
        novoNumero <<= 1;//Desloca um bit para a esquerda
        if (i == posicao) {
          novoNumero |= adBit;
        } else {
          novoNumero |= bit;
        }
        numero <<= 1;//Desloca um bit para a esquerda
      }

    } else {
      System.out.println("Erro: a poiscao [" + posicao + "] nao eh valida");
    }
   return novoNumero;
  }

  /*********************************************
  * Metodo: 
  * Funcao: 
  * Parametros: 
  * Retorno: 
  *********************************************/
  public static int pegarBitNaPosicao(int numero, int posicao) {
    int novoNumero = 0;
    //VERIFICANDO SE A POSICAO PRA ALTERAR ESTA CORRETA
    if (posicao > 0 && posicao <= 32) {
      int displayMask = 1 << 31;//10000000 00000000 00000000 00000000
      //Para cada bit exibe 0 ou 1
      for (int i=1; i<=32; i++) {
        //Utiliza displayMask para isolar o bit
        int bit = (numero & displayMask) == 0 ? 0 : 1;
        novoNumero <<= 1;//Desloca um bit para a esquerda
        if (i == posicao) {
          return bit;
        } else {
          novoNumero |= bit;
        }
        numero <<= 1;//Desloca um bit para a esquerda
      }

    } else {
      System.out.println("Erro: a poiscao [" + posicao + "] nao eh valida");
    }
   return novoNumero;
  }

}//Fim class