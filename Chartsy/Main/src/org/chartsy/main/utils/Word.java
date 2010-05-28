package org.chartsy.main.utils;

import javax.swing.JTextField;

/**
 *
 * @author viorel.gheba
 */
public class Word
{

    private JTextField txtSymbol;
    private int start;
    private int length;

    public Word(JTextField textField)
    {
        txtSymbol = textField;
        start = -1;
        length = 0;
    }

    public void setBounds(int start, int length)
    {
        this.start = Math.max(-1, start);
        this.length = Math.max(0, length);
        if (this.start == -1)
            this.length = 0;
        if (this.length == 0)
            this.start = -1;
    }

    public void increaseLength(int length)
    {
        int max = txtSymbol.getText().length() - this.start;
        this.length = Math.min(max, this.length + length);
        if (this.length == 0)
            this.start = -1;
    }

    public void decreaseLength(int length)
    {
        this.length = Math.max(0, this.length - length);
        if (this.length == 0)
            this.start = -1;
    }

    public int getStart()
    { return this.start; }

    public int getLength()
    { return this.length; }

    public int getEnd()
    { return this.start + this.length; }

    public @Override String toString()
    {
        String result = "";
        result = txtSymbol.getText();
        return result;
    }

}
