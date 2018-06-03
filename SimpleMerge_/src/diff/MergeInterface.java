package diff;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
 
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
 
class CaretEx extends JFrame{
	
    private JTextArea txtArea1 = new JTextArea();
    private JScrollPane scrollPane1 = new JScrollPane(txtArea1);
    public CaretEx(){
        setLocationByPlatform(true);
        setLayout(new GridLayout(1, 2));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 300);
        getContentPane().add(scrollPane1);
        setVisible(true);
        scrollPane1.add(txtArea1);
        scrollPane1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try{
                    int cp = txtArea1.getCaretPosition();
                    int line = txtArea1.getLineOfOffset(cp);
                    int start = txtArea1.getLineStartOffset(line);
                    int end = txtArea1.getLineEndOffset(line);
                    String str = String.format("커서 : %d, 줄 : %d, 시작 : %d, 종료 : %d", cp, line, start, end);
                    
                    //정보표시
                    setTitle(str);
 
                    //강조하기
                    Highlighter highlighter = txtArea1.getHighlighter();
                    highlighter.removeAllHighlights();
                    DefaultHighlightPainter painter = new DefaultHighlightPainter(Color.yellow);
                    highlighter.addHighlight(start, end, painter);
                }catch(Exception error){
                    error.printStackTrace();
                }
            }
        });
        
        setVisible(true);
    }
}
 
public class MergeInterface {
    public static void main(String[] args) {
       CaretEx c= new CaretEx();
    }
}