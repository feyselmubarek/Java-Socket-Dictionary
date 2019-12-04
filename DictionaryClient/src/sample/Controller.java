//1.Ermiyas Gezahegn… ATR /5552/09 ermiyas10080@gmail.com
// 2.Fasil Beshiwork … ATR/9359/09 …. fasilbeshiwork17@gmail.com
// 3. Feysel Mubarek … ATR/5064/09…feyselmubarek@gmail.com
// 4.Habte Assefa… ATR/0081/09…. habteasefa726@gmail.com
// 5. Hana Tesfaye.…. ATR/4224/09…. hanatesfaye223@gmail.com

package sample;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import sample.data.Word;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class Controller implements Initializable {
    @FXML
    public TextFlow textFlow;
    @FXML
    public JFXTextField wordTextField;
    @FXML
    public TextField searchTextField;
    @FXML
    public JFXTextField wordMeaningTextField;
    @FXML
    public JFXListView<Word> jfxListView;
    @FXML
    private JFXSpinner jfxSpinner;
    @FXML
    private Label progressLabel;
    @FXML
    private JFXCheckBox jfxCheckBox;

    private ObservableList<Word> wordList;
    private FilteredList<Word> filteredData;
    private Alert alert;

    private InetAddress ip;
    private Socket s;
    private String dataToSend;

    private DataInputStream dis;
    private DataOutputStream dos;

    public Controller() {
        wordList = FXCollections.observableArrayList();
        alert = new Alert(Alert.AlertType.NONE);
    }

    private void populateJfxListView(String data){
        String wholeData = data;
        Word word;

        String[] lineArray = wholeData.split("~");

        for (int i = 0; i < lineArray.length; i++){
            String[] words = lineArray[i].split("`");

            word = new Word(words[0], words[1]);
            wordList.add(word);
        }
    }

    private void rePopulateList(){
        try{
            dos.writeUTF("g");
            String wholeData = dis.readUTF();

            wordList.clear();
            String[] lineArray = wholeData.split("~");

            for (int i = 0; i < lineArray.length; i++){
                wordList.add(new Word(lineArray[i].split("`")[0], lineArray[i].split("`")[1]));
            }
        }catch(Exception e){

        }
    }

    @FXML
    public void closePressBtn(MouseEvent event){
        Stage stage = (Stage) ((Node)(event.getSource())).getScene().getWindow();
        stage.close();
        try {
            dos.writeUTF("Exit");
            s.close();
        }catch (Exception e){
        }
    }

    @FXML
    public void refreshPressBtn(MouseEvent event){
        rePopulateList();
    }

    @FXML
    public void addNewWord(ActionEvent event){
        if(wordTextField.getText().isEmpty() || wordMeaningTextField.getText().isEmpty()){
            alert.setContentText("Please fill the fields");
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.showAndWait();
        }else {
            Word word = new Word(wordTextField.getText(), wordMeaningTextField.getText());

            if(jfxCheckBox.isSelected()){
                dataToSend = "2`" + word.getTitle() + "`" + word.getDefinition();
            }else{
                dataToSend = "1`" + word.getTitle() + "`" + word.getDefinition();
            }

            System.out.println(dataToSend);

            try{

                dos.writeUTF(dataToSend);
                String result = dis.readUTF();

                if(result.equals("Success")){
                    wordList.add(word);
                }else if(result.equals("Duplicate")){
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setContentText("Word Already Exists!\n" +
                            "If you didn't add the word some one might added it\n" +
                            "For multiple definition check multiple already" +
                            "\nPlease Refresh!");
                    alert.showAndWait();
                }else if(result.equals("Multiple")){
                    wordList.clear();
                    rePopulateList();
                }else{
                    alert.setAlertType(Alert.AlertType.ERROR);
                    alert.setContentText("Error in Adding!!\n" +
                            "Word might not be found Add it with out multiple check marked");
                    alert.showAndWait();
                }

            }catch (Exception e){

            }
        }
    }

    @FXML
    public void clearFields(ActionEvent event){
        wordTextField.setText("");
        wordMeaningTextField.setText("");
    }

    @FXML
    public void removeWord(ActionEvent event){
        ObservableList<Word> names;
        names = jfxListView.getSelectionModel().getSelectedItems();

        dataToSend = "3`" + names.get(0).getTitle() + "`" + names.get(0).getDefinition();
        System.out.println(dataToSend);

        if(!names.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete " + names.get(0).getTitle() +" ?",
                    ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                try{
                    dos.writeUTF(dataToSend);
                    String result = dis.readUTF();

                    if(result.equals("Success")){
                        wordList.remove(wordList.indexOf(names.get(0)));
                    }else{
                        Alert alert2 = new Alert(Alert.AlertType.NONE);
                        alert2.setContentText("Word not Found!!" +
                                "\nOther clients might have deleted it!" +
                                "\nPlease Refresh!!");
                        alert2.setAlertType(Alert.AlertType.ERROR);
                        alert2.showAndWait();
                    }
                }catch (Exception e){}
            }
        }
    }

    @FXML
    public void searchWord(ActionEvent event){
        String filter = searchTextField.getText();

        try{
            dos.writeUTF("4`" + filter);
            String searchedWord = dis.readUTF();
            System.out.println(searchedWord);

            if(!searchedWord.equals("Error")){
                if(filter == null || filter.length() == 0) {
                    filteredData.setPredicate(s -> true);
                }
                else {
                    filteredData.setPredicate(s -> s.getTitle().contains(searchedWord));
                }
            }else{
                System.out.println("alerting");
                alert.setContentText("Word Not Found");
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.showAndWait();
            }
        }catch (Exception e){

        }
    }

    @FXML
    public void minimizePressBtn(MouseEvent event){
        Stage stage = (Stage) ((Node)(event.getSource())).getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ip = InetAddress.getByName("localhost");
            s = new Socket(ip, 5056);
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
            final String[] data = new String[1];

            Thread receivedThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String line = "";
                    try {
                        line = dis.readUTF();
                        data[0] = line;
                        populateJfxListView(line);
                        System.out.println(" =>  Server: " + line);
                    } catch (IOException i) {
                        System.out.println(i);
                    }
                }
            });

            receivedThread.start();

            jfxSpinner.setVisible(false);
            progressLabel.setVisible(false);

        }catch (ConnectException ce){
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText(ce.getMessage());
            alert.showAndWait();

//            jfxSpinner.setVisible(true);
//            progressLabel.setVisible(true);
//            progressLabel.setText(ce.getMessage() + "...");
        }catch (IOException ex){
            System.out.println(ex.toString());
        }

        filteredData = new FilteredList<>(wordList, s -> true);

        searchTextField.textProperty().addListener(obs->{
            String filter = searchTextField.getText();
            try{
                if(filter == null || filter.length() == 0) {
                    filteredData.setPredicate(s -> true);
                }
                else {
                    filteredData.setPredicate(s -> s.getTitle().contains(filter));
                }
            }catch (Exception e){

            }
        });

        jfxListView.setItems(filteredData);

        jfxListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           try{
               ObservableList textList = textFlow.getChildren();
               textList.clear();

               Text text1 = new Text(newValue.toString().toUpperCase() + "\n");
               text1.setFont(new Font(15));
               text1.setFill(Color.DARKSLATEBLUE);

               textList.add(text1);

               String[] multipleDef = newValue.getDefinition().split("@");

               for (String def : multipleDef) {
                   Text text2 = new Text(def + "\n\n");
                   text2.setFill(Color.DARKSLATEGRAY);
                   text2.setFont(new Font(13));

                   textList.add(text2);
               }
           }catch (Exception e){}
        });
    }
}
