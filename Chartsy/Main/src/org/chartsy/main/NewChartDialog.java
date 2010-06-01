package org.chartsy.main;

import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import org.chartsy.main.chart.Chart;
import org.chartsy.main.data.DataProvider;
import org.chartsy.main.data.Exchange;
import org.chartsy.main.data.Stock;
import org.chartsy.main.data.StockNode;
import org.chartsy.main.data.StockSet;
import org.chartsy.main.managers.ChartManager;
import org.chartsy.main.managers.DataProviderManager;
import org.chartsy.main.utils.AutocompletePopup;
import org.chartsy.main.utils.UppercaseDocumentFilter;
import org.chartsy.main.utils.Word;
import org.openide.windows.WindowManager;

/**
 *
 * @author viorel.gheba
 */
public class NewChartDialog extends javax.swing.JDialog {

    private static final String defaultDataProvider = "MrSwing";
    private static final char[] WORD_SEPARATORS = {' ', '\n', '\t', ',', ';', '!', '?', '\'', '(', ')', '[', ']', '\"', '{', '}', '/', '\\', '<', '>'};

    private AutocompletePopup menuWindow;
    private Window owner;
    private Word word;

    private Stock stock = null;
    private DataProvider dataProvider = null;
    private Chart chart = null;
   
    public NewChartDialog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        setTitle("New Chart");
        initComponents();
        parent.setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
        getRootPane().setDefaultButton(btnNewChart);
        owner = parent;
        menuWindow = new AutocompletePopup(txtSymbol);
        word = new Word(txtSymbol);
        initForm();
        setEventManagement();
    }


    private void initForm()
    {
        autocompleteLbl.setVisible(false);
        dataProvider = DataProviderManager.getDefault().getDataProvider(defaultDataProvider);
        List<String> dataProviders = DataProviderManager.getDefault().getDataProviders();
        Collections.sort(dataProviders);
        
        lstDataProvider.setMaximumRowCount(dataProviders.size());
        for (String s : dataProviders)
            lstDataProvider.addItem(s);
        lstDataProvider.setSelectedItem(defaultDataProvider);

        List<String> charts = ChartManager.getDefault().getCharts();
        Collections.sort(charts);
        lstChart.setMaximumRowCount(charts.size());
        for (String s : charts)
            lstChart.addItem(s);

        ((AbstractDocument) txtSymbol.getDocument()).setDocumentFilter(new UppercaseDocumentFilter());
    }

    private void setEventManagement()
    {
        txtSymbol.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                if (menuWindow.isVisible())
                    txtSymbol.requestFocus();
            }
        });

        txtSymbol.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                if (menuWindow.isVisible())
                    menuWindow.setVisible(false);
            }
        });

        txtSymbol.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.isConsumed())
                    return;
                
                if (menuWindow.isVisible())
                {
                    switch (e.getKeyCode())
                    {
                        case KeyEvent.VK_ENTER:
                            menuWindow.onSelected();
                            e.consume();
                            break;
                        case KeyEvent.VK_DOWN:
                            menuWindow.moveDown();
                            e.consume();
                            break;
                        case KeyEvent.VK_UP:
                            menuWindow.moveUp();
                            e.consume();
                            break;
                        case KeyEvent.VK_PAGE_DOWN:
                            menuWindow.movePageDown();
                            e.consume();
                            break;
                        case KeyEvent.VK_PAGE_UP:
                            menuWindow.movePageUp();
                            e.consume();
                            break;
                    }
                }
                else
                {
                    if (!dataProvider.getName().equals("Yahoo"))
                        onControlSpace();
                }
            }
        });

        owner.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentHidden(ComponentEvent e)
            { menuWindow.setVisible(false); }
            @Override
            public void componentMoved(ComponentEvent e)
            {
                if (menuWindow.isVisible())
                    menuWindow.move();
            }
        });
        
        txtSymbol.getDocument().addDocumentListener(new DocumentListener()
        {
            public void insertUpdate(DocumentEvent e)
            {
                if (menuWindow.isVisible())
                {
                    int beginIndex = e.getOffset();
                    int endIndex = beginIndex + e.getLength();
                    String newCharacters = txtSymbol.getText().substring(beginIndex, endIndex);
                    for (int i = 0; i < WORD_SEPARATORS.length; i++)
                    {
                        if (newCharacters.indexOf(WORD_SEPARATORS[i]) != -1)
                        {
                            word.setBounds(-1, 0);
                            menuWindow.setWords(new StockNode[] {});
                            menuWindow.setVisible(false);
                            return;
                        }
                    }
                    word.increaseLength(e.getLength());
                    updateMenu();
                }
            }
            public void removeUpdate(DocumentEvent e)
            {
                if (menuWindow.isVisible())
                {
                    word.decreaseLength(e.getLength());
                    if (word.getLength() == 0)
                    {
                        menuWindow.setWords(new StockNode[] {});
                        menuWindow.setVisible(false);
                        return;
                    }
                    updateMenu();
                }
            }
            public void changedUpdate(DocumentEvent e)
            {}
        });
    }

    private StockNode[] getWords(String word)
    {
        word = word.toLowerCase();
        StockSet returnSet = getSet(word);
        return returnSet.toArray(new StockNode[returnSet.size()]);
    }

    private boolean isWordSeparator(char aChar)
    {
        for (int i = 0; i < WORD_SEPARATORS.length; i++)
        {
            if (aChar == WORD_SEPARATORS[i])
                return true;
        }
        return false;
    }

    private void onControlSpace()
    {
        word = getCurrentTypedWord();
        if (word.getLength() == 0)
            return;
        int index = word.getStart();
        Rectangle rect = null;
        try
        {
            rect = txtSymbol.getUI().modelToView(txtSymbol, index);
        }
        catch (BadLocationException e)
        {}
        if (rect == null)
            return;
        
        menuWindow.show(txtSymbol, rect.x, rect.y + rect.height);
        updateMenu();
        txtSymbol.requestFocus();
    }

    private void updateMenu()
    {
        if (word.getLength() == 0)
            return;
        StockNode[] words = getWords(word.toString());
        menuWindow.setWords(words);
    }

    private Word getCurrentTypedWord() 
    {
        Word w = new Word(txtSymbol);
        int position = txtSymbol.getCaretPosition();
        if (position == 0)
            return w;
        int index = position - 1;
        boolean found = false;
        while ((index > 0) && (!found))
        {
            char current = txtSymbol.getText().charAt(index);
            if (isWordSeparator(current))
            {
                found = true;
                index++;
            }
            else
            {
                index--;
            }
        }
        w.setBounds(index, position - index);
        return w;
    }

    private StockSet getSet(String word)
    {
        StockSet returnSet = dataProvider.getAutocomplete(word);
        return returnSet;
    }

    public Stock getStock() 
    { return stock; }

    public DataProvider getDataProvider() 
    { return dataProvider; }

    public Chart getChart() 
    { return chart; }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        lblSymbol = new javax.swing.JLabel();
        lblChart = new javax.swing.JLabel();
        lblExchange = new javax.swing.JLabel();
        txtSymbol = new javax.swing.JTextField();
        lstChart = new javax.swing.JComboBox();
        lstExchange = new javax.swing.JComboBox();
        lblLogo = new javax.swing.JLabel();
        lstDataProvider = new javax.swing.JComboBox();
        lblDataProvider = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnNewChart = new javax.swing.JButton();
        autocompleteLbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setMinimumSize(new java.awt.Dimension(400, 168));

        lblSymbol.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSymbol.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblSymbol.text")); // NOI18N

        lblChart.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblChart.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblChart.text")); // NOI18N

        lblExchange.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblExchange.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblExchange.text")); // NOI18N

        txtSymbol.setColumns(6);
        txtSymbol.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.txtSymbol.text")); // NOI18N
        txtSymbol.setToolTipText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.txtSymbol.toolTipText")); // NOI18N

        lstExchange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lstExchangeActionPerformed(evt);
            }
        });

        lblLogo.setBackground(new java.awt.Color(255, 255, 255));
        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/chartsy/main/resources/logo.png"))); // NOI18N
        lblLogo.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblLogo.text")); // NOI18N
        lblLogo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lstDataProvider.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lstDataProviderActionPerformed(evt);
            }
        });

        lblDataProvider.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDataProvider.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.lblDataProvider.text")); // NOI18N

        btnCancel.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnNewChart.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.btnNewChart.text")); // NOI18N
        btnNewChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewChartActionPerformed(evt);
            }
        });

        autocompleteLbl.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        autocompleteLbl.setText(org.openide.util.NbBundle.getMessage(NewChartDialog.class, "NewChartDialog.autocompleteLbl.text")); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblLogo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 489, Short.MAX_VALUE)
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(lblSymbol)
                                .add(56, 56, 56)
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(autocompleteLbl)
                                    .add(txtSymbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 361, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(27, 27, 27)))
                        .addContainerGap())
                    .add(mainPanelLayout.createSequentialGroup()
                        .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblChart)
                            .add(mainPanelLayout.createSequentialGroup()
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lblDataProvider)
                                    .add(lblExchange))
                                .add(20, 20, 20)
                                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(lstExchange, 0, 362, Short.MAX_VALUE)
                                    .add(lstChart, 0, 362, Short.MAX_VALUE)
                                    .add(lstDataProvider, 0, 362, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                                        .add(btnNewChart)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(btnCancel)))))
                        .add(36, 36, 36))))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(lblLogo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSymbol)
                    .add(txtSymbol, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3)
                .add(autocompleteLbl)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblDataProvider)
                    .add(lstDataProvider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblExchange)
                    .add(lstExchange, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblChart)
                    .add(lstChart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnNewChart))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        stock = null;
        dataProvider = null;
        chart = null;
        setVisible(false);
}//GEN-LAST:event_btnCancelActionPerformed

    private void btnNewChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewChartActionPerformed
        dataProvider = DataProviderManager.getDefault().getDataProvider((String) lstDataProvider.getSelectedItem());
        Exchange exchange = dataProvider.getExchanges()[lstExchange.getSelectedIndex()];
        stock = new Stock(txtSymbol.getText(), exchange.getSufix());
        chart = ChartManager.getDefault().getChart((String) lstChart.getSelectedItem());
        setVisible(false);
}//GEN-LAST:event_btnNewChartActionPerformed

    private void lstDataProviderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lstDataProviderActionPerformed
        JComboBox list = (JComboBox) evt.getSource();
        String provider = (String) list.getSelectedItem();
        lstExchange.removeAllItems();
        dataProvider = DataProviderManager.getDefault().getDataProvider(provider);
        Exchange[] exchanges = dataProvider.getExchanges();
        if (exchanges == null || exchanges.length == 0)
        {
            lblExchange.setVisible(false);
            lstExchange.setVisible(false);
        }
        else
        {
            for (Exchange exchange : exchanges)
                lstExchange.addItem(exchange.getExchange());
        }
    }//GEN-LAST:event_lstDataProviderActionPerformed

    private void lstExchangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lstExchangeActionPerformed
        Object obj = lstDataProvider.getSelectedItem();
        if (obj != null)
        {
            DataProvider dp = DataProviderManager.getDefault().getDataProvider((String) obj);
            int i = lstExchange.getSelectedIndex();
            if (i != -1)
            {
                Exchange exchange = dp.getExchanges()[i];
                String symbol = txtSymbol.getText();

                if (symbol != null)
                {
                    symbol.trim();
                    String delimiter = ".";

                    if (symbol.contains(delimiter))
                    {
                        int index = symbol.indexOf(delimiter);
                        symbol = symbol.substring(0, index);
                    }

                    txtSymbol.setText(symbol + exchange.getSufix());
                }
            }
        }
    }//GEN-LAST:event_lstExchangeActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewChartDialog dialog = new NewChartDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel autocompleteLbl;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNewChart;
    private javax.swing.JLabel lblChart;
    private javax.swing.JLabel lblDataProvider;
    private javax.swing.JLabel lblExchange;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblSymbol;
    private javax.swing.JComboBox lstChart;
    private javax.swing.JComboBox lstDataProvider;
    private javax.swing.JComboBox lstExchange;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField txtSymbol;
    // End of variables declaration//GEN-END:variables

}
