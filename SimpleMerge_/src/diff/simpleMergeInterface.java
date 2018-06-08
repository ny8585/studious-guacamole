package diff.test;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;;
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
import java.util.ArrayList;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import java.awt.event.*;
import java.io.File;
import diff.test.diff_match_patch;


public class simpleMergeInterface extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextArea txtArea1, txtArea2;
	private JScrollPane scrollPane1;
	private JScrollPane scrollPane2;
	JScrollBar sb;
	boolean isLeft = false, isRight = false;

	int haveMerged = 0;
	String temp1, temp2;
	
	private diff_match_patch diffMatchPatch = new diff_match_patch();
	private LinkedList<diff_match_patch.Diff> linkDiff = new LinkedList<diff_match_patch.Diff>();
	private ArrayList<int[]> leftDiffIndex = new ArrayList<int[]>(), rightDiffIndex = new ArrayList<int[]>();

	private int mouseClickIndex1, mouseClickIndex2;
	private boolean isLeftClick, isRightClick;
	private int m1 = 0, m2 = 0;
	private JMenuBar menuBar = new JMenuBar(); // Window Menu Bar
	private JMenuItem openItem, openItem1, openItem2, saveItem, saveItem1, saveItem2, mergeItem1, mergeItem2,
			undoMenuItem, redoMenuItem, refItem, cutItem, copyItem, pasteItem;

	private HighlightPainter painterY = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
	private HighlightPainter painterP = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);

	private Highlighter highlighter1;
	private Highlighter highlighter2;

	FileDialog openDialog1;
	FileDialog openDialog2;
	File[] files;

	// undo and redo
	private Document editDoc1, editDoc2;
	protected UndoHandler undoHandler = new UndoHandler();
	protected UndoManager undoManager = new UndoManager();
	private UndoAction undoAction = null, undoAction2 = null;
	private RedoAction redoAction = null, redoAction2 = null;

	Runnable doScroll = new Runnable() {
		public void run() {
			txtArea1.setCaretPosition(0);
			txtArea2.setCaretPosition(0);
		}
	};
	Runnable doScroll2 = new Runnable() {
		public void run() {
			if(isLeftClick){
				txtArea1.setCaretPosition(sb.getValue());
			}
			else{
				txtArea2.setCaretPosition(sb.getValue());
			}
		}
	};

	public simpleMergeInterface() {
		openDialog1 = new FileDialog(this, "FileDialog");
		openDialog1.setMultipleMode(true);
		MenuActionListener m = new MenuActionListener();
		TextKeyListener t = new TextKeyListener();
		MouseEvent mouse = new MouseEvent();
	}

	class TextKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			int txt1 = -1, txt2 = -1;
			int Ctxt1 = 0, Ctxt2 = 0;
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
			if (Ctxt1 > Ctxt2) {
				for (int k = 0; k < Ctxt1 - Ctxt2; k++) {
					txtArea2.append("\n");
				}
			}
			if (Ctxt1 < Ctxt2) {
				for (int k = 0; k < Ctxt2 - Ctxt1; k++) {
					txtArea1.append("\n");
				}
			}
		}

		public void keyReleased(KeyEvent e) {

		}

		public void keyTyped(KeyEvent e) {

		}

		public TextKeyListener() {
			txtArea1.addKeyListener(this);
			txtArea2.addKeyListener(this);
		}
	}

	// java undo and redo action classes
	class UndoHandler implements UndoableEditListener {
		/**
		 * Messaged when the Document has created an edit, the edit is added to
		 * <code>undoManager</code>, an instance of UndoManager.
		 */
		
		public void undoableEditHappened(UndoableEditEvent e) {
//			haveMerged = 0;
			undoManager.addEdit(e.getEdit());
			undoAction.update();
			redoAction.update();
//			checkDiff(txtArea1, txtArea2);
		}

	}

	class UndoAction extends AbstractAction {
		public UndoAction() {
			super("Undo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			if(haveMerged==0) {
				try {
					undoManager.undo();
				} catch (CannotUndoException ex) {
					// TODO deal with this
					// ex.printStackTrace();
				}
				update();
				redoAction.update();				
			}
			if(haveMerged==1) {
				temp2 = txtArea1.getText();
				txtArea1.setText(temp1);
//				haveMerged=0;
			}
			if(haveMerged==2) {
				temp2 = txtArea2.getText();
				txtArea2.setText(temp1);
//				haveMerged=0;
			}
		}

		protected void update() {
			if (undoManager.canUndo()) {
				setEnabled(true);
				putValue(Action.NAME, undoManager.getUndoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "Undo");
			}
		}
	}

	class RedoAction extends AbstractAction {
		public RedoAction() {
			super("Redo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			if(haveMerged == 0) {
			try {
				undoManager.redo();
			} catch (CannotRedoException ex) {
				// TODO deal with this
				ex.printStackTrace();
			}
			update();
			undoAction.update();
			}
			if(haveMerged==1) {
				txtArea1.setText(temp2);
//				haveMerged=0;
			}
			if(haveMerged==2) {
				txtArea2.setText(temp2);
//				haveMerged=0;
			}
		}

		protected void update() {
			if (undoManager.canRedo() || haveMerged != 0) {
				setEnabled(true);
				putValue(Action.NAME, undoManager.getRedoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "Redo");
			}
		}
	}

	class MouseEvent implements MouseListener {
		public void mouseClicked(java.awt.event.MouseEvent arg0) {
			// TODO Auto-generated method stub
		}

		public void mouseEntered(java.awt.event.MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void mouseExited(java.awt.event.MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void mousePressed(java.awt.event.MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		public void mouseReleased(java.awt.event.MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

	}

	class MenuActionListener implements ActionListener {
		int m1=0, m2=0;
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

					checkDiff(txtArea1, txtArea2);
				} catch (Exception e2) {
					System.out.println("ERROR : OPEN LEFT FILE");
				}
				break;
			case "Save":
				if (files[0].getPath() == null)
					return; // 이걸빼면 취소를 해도 저장이됨
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(files[0].getPath()));
					writer.write(txtArea1.getText());
					writer.close();
					setTitle(files[0].getName());
				} catch (Exception e2) {
					System.out.println("ERROR : SAVE LEFT FILE");
				}
				if (files[1].getPath() == null)
					return; // 이걸빼면 취소를 해도 저장이됨
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(files[1].getPath()));
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
			case "Merge to Right":
				haveMerged = 2;
				temp1 = txtArea2.getText();
				if (isLeftClick) {
					for (int i = 0; i < leftDiffIndex.size(); i++) {
						if (mouseClickIndex1 >= leftDiffIndex.get(i)[0]
								&& mouseClickIndex1 <= leftDiffIndex.get(i)[1]) {

								txtArea2.replaceRange(
										linkDiff.get(leftDiffIndex.get(i)[2]).text,
										rightDiffIndex.get(i)[0], rightDiffIndex.get(i)[1]);
								sb = scrollPane1.getVerticalScrollBar();
								checkDiff(txtArea1, txtArea2);
								SwingUtilities.invokeLater(doScroll2);
							break;
						}
					}
					
				} else {
					for (int i = 0; i < rightDiffIndex.size(); i++) {
						if (mouseClickIndex2 >= rightDiffIndex.get(i)[0]
								&& mouseClickIndex2 <= rightDiffIndex.get(i)[1]) {
								txtArea2.replaceRange(linkDiff.get(leftDiffIndex.get(i)[2]).text,rightDiffIndex.get(i)[0], rightDiffIndex.get(i)[1]);

								sb = scrollPane1.getVerticalScrollBar();
								checkDiff(txtArea1, txtArea2);
								SwingUtilities.invokeLater(doScroll2);
							break;
						}
					}
				}
				break;
			case "Merge to Left":
				haveMerged = 1;
				temp1 = txtArea1.getText();
				if (isLeftClick) {
					for (int i = 0; i < leftDiffIndex.size(); i++) {
						if (mouseClickIndex1 >= leftDiffIndex.get(i)[0]
								&& mouseClickIndex1 <= leftDiffIndex.get(i)[1]) {
								txtArea1.replaceRange(linkDiff.get(rightDiffIndex.get(i)[2]).text,
										leftDiffIndex.get(i)[0], leftDiffIndex.get(i)[1]);
								sb = scrollPane1.getVerticalScrollBar();
								checkDiff(txtArea1, txtArea2);
								SwingUtilities.invokeLater(doScroll2);
							break;
						}
					}
				} else {
					for (int i = 0; i < rightDiffIndex.size(); i++) {
						if (mouseClickIndex2 >= rightDiffIndex.get(i)[0]
								&& mouseClickIndex2 <= rightDiffIndex.get(i)[1]) {
								txtArea1.replaceRange(linkDiff.get(rightDiffIndex.get(i)[2]).text,
										leftDiffIndex.get(i)[0], leftDiffIndex.get(i)[1]);
								sb = scrollPane1.getVerticalScrollBar();
								checkDiff(txtArea1, txtArea2);
								SwingUtilities.invokeLater(doScroll2);
							break;
						}
					}
					undoAction = new UndoAction();
					redoAction = new RedoAction();
				}
				txtArea1.setText(txtArea1.getText());
				break;
			case "Cut":
				break;
			case "Copy":
				break;
			case "Paste":
				break;
			case "Refresh":
				checkDiff(txtArea1, txtArea2);
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
			txtArea1.addMouseListener(new MouseEvent(){
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e){
					mouseClickIndex1 = txtArea1.getSelectionStart();
					isLeftClick = true;
					isRightClick = false;
					System.out.println(isLeftClick+" "+isRightClick);
				}
			});
			txtArea2.addMouseListener(new MouseEvent(){
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e){
					mouseClickIndex2 = txtArea2.getSelectionStart();
					isLeftClick = false;
					isRightClick = true;
					System.out.println(isLeftClick+" "+isRightClick);
				}
			});
			scrollPane1 = new JScrollPane(txtArea1);
			scrollPane2 = new JScrollPane(txtArea2);

			scrollPane1.getVerticalScrollBar().setModel(scrollPane2.getVerticalScrollBar().getModel());
			scrollPane1.setAutoscrolls(true);
			scrollPane2.setAutoscrolls(true);
			getContentPane().add(scrollPane1);
			getContentPane().add(scrollPane2);

			highlighter1 = txtArea1.getHighlighter();
			highlighter2 = txtArea2.getHighlighter();

			editDoc1 = txtArea1.getDocument();
			editDoc1.addUndoableEditListener(undoHandler);	

			editDoc2 = txtArea2.getDocument();
			editDoc2.addUndoableEditListener(undoHandler);


			undoAction = new UndoAction();
			redoAction = new RedoAction();
//			undoAction2 = new UndoAction();
//			redoAction2 = new RedoAction();

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

			// Merge menu
			JMenu mergeMenu = new JMenu("Merge");
			mergeMenu.setMnemonic('M');
			mergeItem1 = mergeMenu.add("Merge to Right");
			mergeItem2 = mergeMenu.add("Merge to Left");

			// Edit menu
			JMenu editMenu = new JMenu("Edit");
			editMenu.setMnemonic('E');
			// undoMenuItem = editMenu.add("Undo");
			// redoMenuItem = editMenu.add("Redo");
			undoMenuItem = new JMenuItem(undoAction);
			redoMenuItem = new JMenuItem(redoAction);//
			
			editMenu.add(undoMenuItem);
			editMenu.add(redoMenuItem);
			editMenu.addSeparator();
			
			cutItem = new JMenuItem(new DefaultEditorKit.CutAction());
			cutItem.setText("Cut");
			editMenu.add(cutItem);
			
			copyItem = new JMenuItem(new DefaultEditorKit.CopyAction());
			copyItem.setText("Copy");
			editMenu.add(copyItem);
			
			pasteItem = new JMenuItem(new DefaultEditorKit.PasteAction()); 
			pasteItem.setText("Paste");
			editMenu.add(pasteItem);
			editMenu.addSeparator();
			
			refItem = editMenu.add("Refresh");
			
			menuBar.add(fileMenu);
			menuBar.add(mergeMenu);
			menuBar.add(editMenu);

			openItem.setAccelerator(KeyStroke.getKeyStroke('O', CTRL_DOWN_MASK));
			saveItem.setAccelerator(KeyStroke.getKeyStroke('S', CTRL_DOWN_MASK));
			undoMenuItem.setAccelerator(KeyStroke.getKeyStroke('Z', CTRL_DOWN_MASK));
			redoMenuItem.setAccelerator(KeyStroke.getKeyStroke('Y', CTRL_DOWN_MASK));
			refItem.setAccelerator(KeyStroke.getKeyStroke('R', CTRL_DOWN_MASK));
			openItem.addActionListener(this);
			saveItem.addActionListener(this);
			saveItem1.addActionListener(this);
			saveItem2.addActionListener(this);
			mergeItem1.addActionListener(this);
			mergeItem2.addActionListener(this);
			refItem.addActionListener(this);
		}

	}

	void checkDiff(JTextArea txtArea1, JTextArea txtArea2) {
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
				if (i == 0) {
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
								insertNum = insert.indexOf("\n", insertNum + 1);
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
							if (insertCount > deleteCount) {
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
							insertNum = insert.indexOf("\n", insertNum + 1);
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
								insertNum = insert.indexOf("\n", insertNum + 1);
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
							if (insertCount > deleteCount) {
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
							insertNum = insert.indexOf("\n", insertNum + 1);
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
		for (diff_match_patch.Diff d : linkDiff) {
			if (d.operation == diff_match_patch.Operation.DELETE
					|| d.operation == diff_match_patch.Operation.EQUAL) {
				txtArea1.append(d.text);
			}
		}
		int p0 = 0;
		int p1 = 0;
		int index = 0;
		//leftDiffIndex.clear();
		leftDiffIndex = new ArrayList<int[]>();
		for (diff_match_patch.Diff d : linkDiff) {
			if (d.operation == diff_match_patch.Operation.DELETE
					|| d.operation == diff_match_patch.Operation.EQUAL) {
				p1 = p0 + d.text.length();
				if (d.operation == diff_match_patch.Operation.DELETE) {
					int[] p = new int[3];
					p[2] = index;
					p[0] = p0;
					p[1] = p1;
					leftDiffIndex.add(p);
					try {
						highlighter1.addHighlight(p0, p1, painterY);
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				p0 = p1;
			}
			index++;
		}

		for (diff_match_patch.Diff d : linkDiff) {
			if (d.operation == diff_match_patch.Operation.INSERT
					|| d.operation == diff_match_patch.Operation.EQUAL) {
				txtArea2.append(d.text);
			}
		}
		p0 = 0;
		p1 = 0;
		index = 0;
		//rightDiffIndex.clear();
		rightDiffIndex = new ArrayList<int[]>();
		for (diff_match_patch.Diff d : linkDiff) {
			if (d.operation == diff_match_patch.Operation.INSERT
					|| d.operation == diff_match_patch.Operation.EQUAL) {
				p1 = p0 + d.text.length();
				if (d.operation == diff_match_patch.Operation.INSERT) {
					int[] p = new int[3];
					p[2] = index;
					p[0] = p0;
					p[1] = p1;
					rightDiffIndex.add(p);
					try {
						highlighter2.addHighlight(p0, p1, painterY);
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				p0 = p1;
			}
			index++;
		}
	}

	public static void main(String[] a) {
		simpleMergeInterface window = new simpleMergeInterface();
		window.setBounds(100, 80, 1000, 600);
		window.setVisible(true);
	}
}
