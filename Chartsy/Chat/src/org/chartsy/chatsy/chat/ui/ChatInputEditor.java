package org.chartsy.chatsy.chat.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

public class ChatInputEditor extends ChatArea implements DocumentListener
{

	private final UndoManager undoManager;
    private KeyStroke keyStroke;

    public ChatInputEditor()
	{
        undoManager = new UndoManager();
        this.setDragEnabled(true);
        this.getDocument().addUndoableEditListener(undoManager);
        Action undo = new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
                undoManager.undo();
            }
        };
        keyStroke = KeyStroke.getKeyStroke('z', ActionEvent.CTRL_MASK);
        this.getInputMap().put(keyStroke, "undo");
        this.registerKeyboardAction(undo, KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        this.getDocument().addDocumentListener((DocumentListener)this);
        this.addMouseListener((MouseListener)this);
    }

    public void insertUpdate(DocumentEvent e)
	{
        this.requestFocusInWindow();
    }

    public void setText(String str)
	{
    }

    public void removeUpdate(DocumentEvent e)
	{
    }

    public void changedUpdate(DocumentEvent e)
	{
    }

    public void close()
	{
        this.getDocument().removeDocumentListener(this);
        this.getDocument().removeUndoableEditListener(undoManager);
        this.removeMouseListener(this);
        this.getInputMap().remove(keyStroke);
    }

    public void showAsDisabled()
	{
        this.setEditable(false);
        this.setEnabled(false);
        clear();
        final Color disabledColor = Color.decode("0xeeeeee");
        this.setBackground(disabledColor);
    }

    public void showEnabled()
	{
        this.setEditable(true);
        this.setEnabled(true);
        this.setBackground(Color.white);
    }

}