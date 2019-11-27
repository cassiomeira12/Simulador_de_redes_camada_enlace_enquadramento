/***********************************************************************
* Autor: Cassio Meira Silva
* Matricula: 201610373
* Inicio: 12/03/18
* Ultima alteracao: 14/03/18
* Nome: Configuracao
* Funcao: Criar os ComboBox para o usuario escolher alguma configuracao
***********************************************************************/

package view.componentes;

import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;


public class Configuracao extends AnchorPane {

  public Componente enquadramento;//Configuracoes de ENQUADRAMENTO

  /*********************************************
  * Metodo: Configuracao - Construtor
  * Funcao: Constroi objetos da classe Configuracao
  * Parametros: void
  *********************************************/
  public Configuracao(int posX, int posY) {
    this.setLayoutX(posX);//Definindo a posicao X
    this.setLayoutY(posY);//Definindo a posicao Y


    this.enquadramento = new Componente("Enquadramento");
    this.enquadramento.setItens("CONTAGEM DE CARACTERES","INSERCAO DE BYTES","INSERCAO DE BITS","VIOLACAO DA CAMADA FISICA");
    this.getChildren().add(enquadramento);
  }

  /*********************************************
  * Metodo: setDisable
  * Funcao: Habilita ou Desabilita o componente
  * Parametros: disable : Boolean
  * Retorno : void
  *********************************************/
  public void setDisable(Boolean disable) {
    this.enquadramento.setDisable(disable);
  }


  /***********************************************************************
  * Nome: Componente
  * Funcao: Criar os ComboBox para o usuario escolher alguma configuracao
  ***********************************************************************/
  public class Componente extends VBox {
    private Label titulo;
    private ComboBox<String> combo;

    /*********************************************
    * Metodo: Configuracao - Construtor
    * Funcao: Constroi objetos da classe Configuracao
    * Parametros: void
    *********************************************/
    public Componente(String descricao) {
      this.titulo = new Label(descricao);
      this.combo = new ComboBox<>();
      this.getChildren().add(titulo);
      this.getChildren().add(combo);
    }

    /*********************************************
    * Metodo: setItens
    * Funcao: Adiciona Itens no ComboBox
    * Parametros: item : String
    * Retorno : void
    *********************************************/
    public void setItens(String... item) {
      this.combo.getItems().addAll(item);
      this.combo.getSelectionModel().select(0);
    }

    /*********************************************
    * Metodo: getIndiceSelecionado
    * Funcao: Retorna o indice do Item selecionado
    * Parametros: void
    * Retorno : index : int
    *********************************************/
    public int getIndiceSelecionado() {
      return combo.getSelectionModel().getSelectedIndex();
    }

  }

}//Fim class