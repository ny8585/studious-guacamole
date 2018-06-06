package diff;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import java.awt.event.*;
import java.io.File;
import diff.diff_match_patch;

public class simpleMergeInterface extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextArea txtArea1, txtArea2;
	private JScrollPane scrollPane1;
	private JScrollPane scrollPane2;

	boolean isLeft = false, isRight = false;

	diff_match_patch diffMatchPatch = new diff_match_patch();
	LinkedList<diff_match_patch.Diff> linkDiff = new LinkedList<diff_match_patch.Diff>();

	private JMenuBar menuBar = new JMenuBar(); // Window Menu Bar
	private JMenuItem openItem, openItem1, openItem2, saveItem, saveItem1, saveItem2;

	private HighlightPainter painterY = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
	private HighlightPainter painterP = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);

	private Highlighter highlighter1;
	private Highlighter highlighter2;

	private String leftString, rightString; // diff_match_patch 위한 string
	FileDialog openDialog1;
	FileDialog openDialog2;
	File[] files;
	Runnable doScroll = new Runnable() {
		public void run() {
			txtArea1.setCaretPosition(0);
			txtArea2.setCaretPosition(0);
		}
	};
	Runnable doScroll2 = new Runnable(){
		public void run(){
			txtArea1.setCaretPosition(txtArea1.getDocument().getLength());
			txtArea2.setCaretPosition(txtArea2.getDocument().getLength());
		}
	};

	public simpleMergeInterface() {
		openDialog1 = new FileDialog(this, "FileDialog");
		openDialog1.setMultipleMode(true);
		MenuActionListener m = new MenuActionListener();
		TextKeyListener t = new TextKeyListener();
	}
	class TextKeyListener implements KeyListener{
		public void keyPressed(KeyEvent e){
			int txt1=-1,txt2=-1;
			int Ctxt1=0,Ctxt2=0;
			while (true) {
				txt1 = txtArea1.getText().indexOf("\n", txt1 + 1);
				if (txt1 != -1) {
					Ctxt1++;
				} else
					break;
			}
			while (true) {
				txt2 = txtArea2.getText().indexOf("\n", txt2 + 1);
				if (txt2 != -1) {
					Ctxt2++;
				} else
					break;
			}
			if(Ctxt1>Ctxt2){
				for (int k = 0; k < Ctxt1-Ctxt2; k++) {
					txtArea2.append("\n");
				}
			}
			if(Ctxt1<Ctxt2){
				for (int k = 0; k < Ctxt2-Ctxt1; k++) {
					txtArea1.append("\n");
				}
			}
		}
		public void keyReleased(KeyEvent e){ 
			
		}
		public void keyTyped(KeyEvent e){ 
			
		}
		public TextKeyListener(){
			txtArea1.addKeyListener(this);
			txtArea2.addKeyListener(this);
		}
	}
	class MenuActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			/////////////////////////////////
			String cmd = e.getActionCommand();
			switch (cmd) {
			case "Open":
				openDialog1.setDirectory(".");
				openDialog1.setVisible(true);
				files = openDialog1.getFiles();
				if (files.length != 2)
					return;

				String dfName1 = files[0].getPath();
				String dfName2 = files[1].getPath();
				
				try {
					BufferedReader reader = new BufferedReader(new FileReader(dfName1));
					txtArea1.setText("");
					SwingUtilities.invokeLater(doScroll);
					String line;
					while ((line = reader.readLine()) != null) {
						txtArea1.append(line + "\n"); // 한줄씩 TextArea에 추가
					}
					reader.close();
					
					setTitle(openDialog1.getFile());
					BufferedReader reader2 = new BufferedReader(new FileReader(dfName2));
					txtArea2.setText("");
					
					while ((line = reader2.readLine()) != null) {
						txtArea2.append(line + "\n"); // 한줄씩 TextArea에 추가
					}
					reader2.close();
					
					checkDiff();
					for (diff_match_patch.Diff d : linkDiff) {
						if (d.operation == diff_match_patch.Operation.DELETE
								|| d.operation == diff_match_patch.Operation.EQUAL) {
							txtArea1.append(d.text);
						}
					}
					for (diff_match_patch.Diff d : linkDiff) {
						if (d.operation == diff_match_patch.Operation.DELETE) {
							System.out.println(d.text);
							int p0 = txtArea1.getText().indexOf(d.text);
							int p1 = p0 + d.text.length();
							highlighter1.addHighlight(p0, p1, painterY);
						}
					}

					for (diff_match_patch.Diff d : linkDiff) {
						if (d.operation == diff_match_patch.Operation.INSERT
								|| d.operation == diff_match_patch.Operation.EQUAL) {
							txtArea2.append(d.text);
						}
					}
					for (diff_match_patch.Diff d : linkDiff) {
						if (d.operation == diff_match_patch.Operation.INSERT) {
							System.out.println(d.text);
							int p0 = txtArea2.getText().indexOf(d.text);
							int p1 = p0 + d.text.length();
							highlighter2.addHighlight(p0, p1, painterY);
						}
					}
				} catch (Exception e2) {
					System.out.println("ERROR : OPEN LEFT FILE");
				}
				break;
			case "Save":
				if (files[0].getPath() == null)
					return; // 이걸빼면 취소를 해도 저장이됨
				try {
					BufferedWriter writer = new BufferedWriter(
							new FileWriter(files[0].getPath()));
					writer.write(txtArea1.getText());
					writer.close();
					setTitle(files[0].getName());
				} catch (Exception e2) {
					System.out.println("ERROR : SAVE LEFT FILE");
				}
				if (files[1].getPath() == null)
					return; // 이걸빼면 취소를 해도 저장이됨
				try {
					BufferedWriter writer = new BufferedWriter(
							new FileWriter(files[1].getPath()));
					writer.write(txtArea2.getText());
					writer.close();
					setTitle(files[1].getName());
				} catch (Exception e2) {
					System.out.println("ERROR : SAVE RIGHT FILE");
				}
				// save changed file in the same file route, name
				break;
			case "Save Left As...":
				// 1.FileDialog를 열어 저장 경로 및 파일명 지정
				FileDialog saveDialog1 = new FileDialog(new simpleMergeInterface(), "저장", FileDialog.SAVE);
				saveDialog1.setDirectory("."); // .은 지금폴더
				saveDialog1.setVisible(true);
				// 2. FileDialog가 비정상 종료되었을때
				if (saveDialog1.getFile() == null)
					return; // 이걸빼면 취소를 해도 저장이됨
				String savedfName1 = saveDialog1.getDirectory() + saveDialog1.getFile();
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(savedfName1));
					writer.write(txtArea2.getText());
					writer.close();
					setTitle(saveDialog1.getFile() + " - 메모장");
				} catch (Exception e2) {
					System.out.println("ERROR : SAVE LEFT FILE");
				}
				// save changed file in the same file route, name
				break;

			case "Save Right As...":
				// 1.FileDialog를 열어 저장 경로 및 파일명 지정
				FileDialog saveDialog2 = new FileDialog(new simpleMergeInterface(), "저장", FileDialog.SAVE);
				saveDialog2.setDirectory("."); // .은 지금폴더
				saveDialog2.setVisible(true);
				// 2. FileDialog가 비정상 종료되었을때
				if (saveDialog2.getFile() == null)
					return; // 이걸빼면 취소를 해도 저장이됨
				String savedfName2 = saveDialog2.getDirectory() + saveDialog2.getFile();
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(savedfName2));
					writer.write(txtArea2.getText());
					writer.close();
					setTitle(saveDialog2.getFile() + " - 메모장");
				} catch (Exception e2) {
					System.out.println("ERROR : SAVE RIGHT FILE");
				}
				// save changed file in the same file route, name
				break;

			}
		}

		public MenuActionListener() {
			InitLayout();//
			setDefaultCloseOperation(EXIT_ON_CLOSE);

		}

		public void InitLayout() {
			setLocationByPlatform(true);
			setLayout(new GridLayout(1, 2));
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			txtArea1 = new JTextArea();
			txtArea2 = new JTextArea();

			scrollPane1 = new JScrollPane(txtArea1);
			scrollPane2 = new JScrollPane(txtArea2);
			
			scrollPane1.getVerticalScrollBar().setModel(scrollPane2.getVerticalScrollBar().getModel());
			scrollPane1.setAutoscrolls(true);
			scrollPane2.setAutoscrolls(true);
			getContentPane().add(scrollPane1);
			getContentPane().add(scrollPane2);

			highlighter1 = txtArea1.getHighlighter();
			highlighter2 = txtArea2.getHighlighter();

			JMenu fileMenu = new JMenu("File"); // Create File menu
			fileMenu.setMnemonic('F'); // Create shortcut
			setJMenuBar(menuBar);
			// newItem = fileMenu.add("New");
			openItem = fileMenu.add("Open");
			// closeItem = fileMenu.add("Close");
			fileMenu.addSeparator();
			saveItem = fileMenu.add("Save");
			saveItem1 = fileMenu.add("Save Left As...");
			saveItem2 = fileMenu.add("Save Right As...");
			fileMenu.addSeparator();
			// printItem = fileMenu.add("Print");

			menuBar.add(fileMenu);
			openItem.setAccelerator(KeyStroke.getKeyStroke('O', CTRL_DOWN_MASK));
			saveItem.setAccelerator(KeyStroke.getKeyStroke('S', CTRL_DOWN_MASK));
			openItem.addActionListener(this);
			saveItem.addActionListener(this);
			saveItem1.addActionListener(this);
			saveItem2.addActionListener(this);
		}

	}

	void checkDiff() {
		if (txtArea1.getText() != null & txtArea2.getText() != null) {
			linkDiff = diffMatchPatch.diff_main(txtArea1.getText(), txtArea2.getText());
			diffMatchPatch.diff_cleanupSemantic(linkDiff);
			String delete = "", insert = "";
			int deleteNum = -1, insertNum = -1;
			int deleteCount = 0, insertCount = 0;
			for (int i = 0; i < linkDiff.size() - 2; i++) {
				delete = "";
				insert = "";
				deleteNum = -1;
				insertNum = -1;
				deleteCount = 0;
				insertCount = 0;
				if(i==0){
				if (linkDiff.get(i).operation == diff_match_patch.Operation.DELETE) {
					if (linkDiff.get(i + 1).operation == diff_match_patch.Operation.INSERT) {
						// 맞는게 있음
						delete = linkDiff.get(i).text;
						insert = linkDiff.get(i + 1).text;
						while (true) {
							deleteNum = delete.indexOf("\n", deleteNum + 1);
							if (deleteNum != -1) {
								deleteCount++;
							} else
								break;
						}
						while (true) {
							insertNum = delete.indexOf("\n", deleteNum + 1);
							if (insertNum != -1) {
								insertCount++;
							} else
								break;
						}
						if (deleteCount > insertCount) {
							for (int k = 0; k < deleteCount - insertCount; k++) {
								insert = insert + "\n";
							}
							linkDiff.get(i + 1).text = insert;
						}
						if (insertCount < deleteCount) {
							for (int l = 0; l < insertCount - deleteCount; l++) {
								delete = delete + "\n";
							}
							linkDiff.get(i).text = delete;
						}

					} else {
						// delete만 있음
						delete = linkDiff.get(i).text;
						while (true) {
							deleteNum = delete.indexOf("\n", deleteNum + 1);
							if (deleteNum != -1) {
								deleteCount++;
							} else
								break;
						}
						for (int k = 0; k < deleteCount; k++) {
							insert = insert + "\n";
						}
						linkDiff.add(i + 1, new diff_match_patch.Diff(diff_match_patch.Operation.INSERT, insert));
					}

				}
				if (linkDiff.get(i).operation == diff_match_patch.Operation.INSERT) {// insert만
																							// 있음
					insert = linkDiff.get(i).text;
					while (true) {
						deleteNum = insert.indexOf("\n", deleteNum + 1);
						if (insertNum != -1) {
							insertCount++;
						} else
							break;
					}
					for (int k = 0; k < insertCount; k++) {
						delete = delete + "\n";
					}
					linkDiff.add(i, new diff_match_patch.Diff(diff_match_patch.Operation.DELETE, delete));
				}
				}
				
				if (linkDiff.get(i).operation == diff_match_patch.Operation.EQUAL) {
					if (linkDiff.get(i + 1).operation == diff_match_patch.Operation.DELETE) {
						if (linkDiff.get(i + 2).operation == diff_match_patch.Operation.INSERT) {
							// 맞는게 있음
							delete = linkDiff.get(i + 1).text;
							insert = linkDiff.get(i + 2).text;
							while (true) {
								deleteNum = delete.indexOf("\n", deleteNum + 1);
								if (deleteNum != -1) {
									deleteCount++;
								} else
									break;
							}
							while (true) {
								insertNum = delete.indexOf("\n", deleteNum + 1);
								if (insertNum != -1) {
									insertCount++;
								} else
									break;
							}
							if (deleteCount > insertCount) {
								for (int k = 0; k < deleteCount - insertCount; k++) {
									insert = insert + "\n";
								}
								linkDiff.get(i + 2).text = insert;
							}
							if (insertCount < deleteCount) {
								for (int l = 0; l < insertCount - deleteCount; l++) {
									delete = delete + "\n";
								}
								linkDiff.get(i + 1).text = delete;
							}

						} else {
							// delete만 있음
							delete = linkDiff.get(i + 1).text;
							while (true) {
								deleteNum = delete.indexOf("\n", deleteNum + 1);
								if (deleteNum != -1) {
									deleteCount++;
								} else
									break;
							}
							for (int k = 0; k < deleteCount; k++) {
								insert = insert + "\n";
							}
							linkDiff.add(i + 2, new diff_match_patch.Diff(diff_match_patch.Operation.INSERT, insert));
						}

					}
					if (linkDiff.get(i + 1).operation == diff_match_patch.Operation.INSERT) {// insert만
																								// 있음
						insert = linkDiff.get(i + 1).text;
						while (true) {
							deleteNum = insert.indexOf("\n", deleteNum + 1);
							if (insertNum != -1) {
								insertCount++;
							} else
								break;
						}
						for (int k = 0; k < insertCount; k++) {
							delete = delete + "\n";
						}
						linkDiff.add(i + 1, new diff_match_patch.Diff(diff_match_patch.Operation.DELETE, delete));
					}
				}

			}
			txtArea1.setText("");
			txtArea2.setText("");
		}
	}

	public static void main(String[] a) {
		simpleMergeInterface window = new simpleMergeInterface();
		window.setBounds(100, 80, 1000, 600);
		window.setVisible(true);
	}
}
