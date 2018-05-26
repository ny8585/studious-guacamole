package SimpleMerge;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;

import java.awt.Button;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.event.*;

public class simpleMergeInterface extends JFrame {
   private static final long serialVersionUID = 1L;
   TextArea txtArea1, txtArea2;
   
   public simpleMergeInterface(){
      MenuActionListener m = new MenuActionListener();
   }
   class MenuActionListener implements ActionListener {

      public void actionPerformed(ActionEvent e) {
         String cmd = e.getActionCommand();
         switch (cmd) {
         case "Open Left":
            FileDialog openDialog1 = new FileDialog(new simpleMergeInterface(), "열기", FileDialog.LOAD);
            openDialog1.setDirectory(".");   // .은 지금폴더
            openDialog1.setVisible(true);
            if(openDialog1.getFile() == null) return;
            String dfName1 = openDialog1.getDirectory() + openDialog1.getFile();
            try {
               BufferedReader reader = new BufferedReader(new FileReader(dfName1));
               txtArea1.setText("");
               String line;
               while((line = reader.readLine()) != null) { //읽을 문자열이 없을때까지 반복
                  txtArea1.append(line + "\n");       //한줄씩 TextArea에 추가
               }
               reader.close();
               setTitle(openDialog1.getFile());
            } catch (Exception e2) {
               System.out.println("열기 오류");
            }
            // left, right file open -> left file만 없로드 하고 싶으면 right 빈칸
            break;
         case "Open Right":
             FileDialog openDialog2 = new FileDialog(new simpleMergeInterface(), "열기", FileDialog.LOAD);
             openDialog2.setDirectory(".");   // .은 지금폴더
             openDialog2.setVisible(true);
             if(openDialog2.getFile() == null) return;
             String dfname2 = openDialog2.getDirectory() + openDialog2.getFile();
             try {
                BufferedReader reader = new BufferedReader(new FileReader(dfname2));
                txtArea2.setText("");
                String line;
                while((line = reader.readLine()) != null) { //읽을 문자열이 없을때까지 반복
                   txtArea2.append(line + "\n");       //한줄씩 TextArea에 추가
                }
                reader.close();
                setTitle(openDialog2.getFile());
             } catch (Exception e2) {
                System.out.println("열기 오류");
             }
             // left, right file open -> left file만 없로드 하고 싶으면 right 빈칸
             break;

         case "Save Left":
            // 1.FileDialog를 열어 저장 경로 및 파일명 지정
            FileDialog saveDialog1 = new FileDialog(new simpleMergeInterface(), "저장", FileDialog.SAVE);
            saveDialog1.setDirectory(".");   // .은 지금폴더
            saveDialog1.setVisible(true);
            // 2. FileDialog가 비정상 종료되었을때
            if(saveDialog1.getFile() == null) return; // 이걸빼면 취소를 해도 저장이됨
            String savedfName1 = saveDialog1.getDirectory() + saveDialog1.getFile();
            try {
               BufferedWriter writer = new BufferedWriter(new FileWriter(savedfName1));
               writer.write(txtArea1.getText());
               writer.close();
               setTitle(saveDialog1.getFile() + " - 메모장" );
            } catch (Exception e2) {
               System.out.println("저장 오류");
            }
            // save changed file in the same file route, name
            break;
         case "Save Right":
             // 1.FileDialog를 열어 저장 경로 및 파일명 지정
             FileDialog saveDialog2 = new FileDialog(new simpleMergeInterface(), "저장", FileDialog.SAVE);
             saveDialog2.setDirectory(".");   // .은 지금폴더
             saveDialog2.setVisible(true);
             // 2. FileDialog가 비정상 종료되었을때
             if(saveDialog2.getFile() == null) return; // 이걸빼면 취소를 해도 저장이됨
             String savedfName2 = saveDialog2.getDirectory() + saveDialog2.getFile();
             try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(savedfName2));
                writer.write(txtArea2.getText());
                writer.close();
                setTitle(saveDialog2.getFile() + " - 메모장" );
             } catch (Exception e2) {
                System.out.println("저장 오류");
             }
             // save changed file in the same file route, name
             break;

         case "Save As...":
            // save changed file in route, name what you want
            break;

         }
      }
      public MenuActionListener() {
         InitLayout();//
         setDefaultCloseOperation(EXIT_ON_CLOSE);

      }

      public void InitLayout() {
         setLayout(new GridLayout(1, 2));
         txtArea1 = new TextArea();        
         txtArea2 = new TextArea();
         add(txtArea1);
         add(txtArea2);
         
         JMenu fileMenu = new JMenu("File"); // Create File menu
         fileMenu.setMnemonic('F'); // Create shortcut
         setJMenuBar(menuBar);
         newItem = fileMenu.add("New");
         openItem1 = fileMenu.add("Open Left");
         openItem2 = fileMenu.add("Open Right");
         closeItem = fileMenu.add("Close");
         fileMenu.addSeparator();
         saveItem1 = fileMenu.add("Save Left");
         saveItem2 = fileMenu.add("Save Right");
         saveAsItem = fileMenu.add("Save As...");
         fileMenu.addSeparator();
         printItem = fileMenu.add("Print");

         menuBar.add(fileMenu);
         newItem.setAccelerator(KeyStroke.getKeyStroke('N', CTRL_DOWN_MASK));
         openItem1.setAccelerator(KeyStroke.getKeyStroke('O', CTRL_DOWN_MASK));
         openItem2.setAccelerator(KeyStroke.getKeyStroke('I', CTRL_DOWN_MASK));

         saveItem1.setAccelerator(KeyStroke.getKeyStroke('S', CTRL_DOWN_MASK));
         saveItem2.setAccelerator(KeyStroke.getKeyStroke('D', CTRL_DOWN_MASK));

         printItem.setAccelerator(KeyStroke.getKeyStroke('P', CTRL_DOWN_MASK));
         newItem.addActionListener(this);
         openItem1.addActionListener(this);
         openItem2.addActionListener(this);
         saveItem1.addActionListener(this);
         saveItem2.addActionListener(this);
      }

   }

   private JMenuBar menuBar = new JMenuBar(); // Window menu bar
   private JMenuItem newItem, openItem1, openItem2, closeItem, saveItem1, saveItem2, saveAsItem, printItem;

   
   public static void main(String[] a) {
      simpleMergeInterface window = new simpleMergeInterface();
      window.setBounds(100, 80, 1000, 600);
      window.setVisible(true);
   }
}