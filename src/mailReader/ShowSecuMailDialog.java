
package mailReader;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.PrintStream;
import JavaLib.*;
import dataPack.SingleContact;
import java.util.Enumeration;
import java.util.StringTokenizer;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;
import rsaPack.PGPEncDec;
import sun.net.smtp.SmtpClient;

/**
 *
 * @author  Ravee
 */
public class ShowSecuMailDialog extends javax.swing.JDialog {
    MainForm parent;
    SingleMail sm;
    
    /** Creates new form MessageBox */
    public ShowSecuMailDialog(MainForm parent, SingleMail sm) {
        super(parent, true);

        this.parent = parent;
        this.sm = sm;
        
        initComponents();
        
        Dimension sd  = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(sd.width / 2 - this.getWidth()/ 2, sd.height / 2 - this.getHeight()/ 2);

        decryptMail();

    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextMailSubject = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextMailMessage = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jTextMailFrom = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 153, 153));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel5.setBackground(new java.awt.Color(0, 153, 153));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel8.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("SECU MAIL");
        jLabel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("From ");
        jLabel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel10.setForeground(new java.awt.Color(240, 240, 240));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Subject ");
        jLabel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Message ");
        jLabel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextMailSubject.setBackground(new java.awt.Color(51, 51, 51));
        jTextMailSubject.setEditable(false);
        jTextMailSubject.setForeground(new java.awt.Color(255, 255, 0));

        jTextMailMessage.setBackground(new java.awt.Color(51, 51, 51));
        jTextMailMessage.setColumns(20);
        jTextMailMessage.setEditable(false);
        jTextMailMessage.setForeground(new java.awt.Color(255, 255, 0));
        jTextMailMessage.setRows(5);
        jScrollPane5.setViewportView(jTextMailMessage);

        jButton1.setText("BACK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextMailFrom.setBackground(new java.awt.Color(51, 51, 51));
        jTextMailFrom.setEditable(false);
        jTextMailFrom.setForeground(new java.awt.Color(255, 255, 0));

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextMailFrom, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE))
                    .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(jLabel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextMailSubject, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE))
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(jLabel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE)
                            .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(jTextMailFrom, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(jTextMailSubject, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel11)
                    .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        new LoadForm();

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void decryptMail() {
        String data = sm.msg;
        if(data.equals("")) {
            return;
        }
        
        // data collected start parsing...
        IXMLParser xmlParser;
        IXMLReader xmlReader;
        try
        {
            // Create the XML parser
            xmlParser = XMLParserFactory.createDefaultXMLParser();
            xmlReader = StdXMLReader.stringReader(data);
            xmlParser.setReader(xmlReader);

            // Read stream and parse it!
            IXMLElement xmlDoc = (IXMLElement) xmlParser.parse();
            Enumeration e1 = xmlDoc.getFirstChildNamed("secucontents").enumerateChildren();

            PGPEncDec p = new PGPEncDec(parent.db.rsaPrivate, parent.db.rsaModulus);
            
            while(e1.hasMoreElements()) {
                IXMLElement node = (IXMLElement)e1.nextElement();
                if(node.getName().equals("from")) {
                    String from = node.getContent();
                    StringTokenizer st = new StringTokenizer(from,",");
                    String fromDec = "";
                    while(st.hasMoreTokens()) {
                        String token = st.nextToken().trim();
                        try {
                            long cipherT = Long.parseLong(token);
                            fromDec += (char)(p.process(cipherT));
                        }catch(Exception e) {
                            System.out.println("Exception: " + e);
                        }
                    }
                    System.out.println("From: " + fromDec);
                    jTextMailFrom.setText(fromDec);
                }else if(node.getName().equals("subject")) {
                    String subject = node.getContent();
                    StringTokenizer st = new StringTokenizer(subject,",");
                    String subjectDec = "";
                    while(st.hasMoreTokens()) {
                        String token = st.nextToken().trim();
                        try {
                            long cipherT = Long.parseLong(token);
                            subjectDec += (char)(p.process(cipherT));
                        }catch(Exception e) {
                            System.out.println("Exception: " + e);
                        }
                    }
                    jTextMailSubject.setText(subjectDec);
                }else if(node.getName().equals("message")) {
                    String message = node.getContent();
                    StringTokenizer st = new StringTokenizer(message,",");
                    String messageDec = "";
                    while(st.hasMoreTokens()) {
                        String token = st.nextToken().trim();
                        try {
                            long cipherT = Long.parseLong(token);
                            messageDec += (char)(p.process(cipherT));
                        }catch(Exception e) {
                            System.out.println("Exception: " + e);
                        }
                    }
                    jTextMailMessage.setText(messageDec);

                }
            }
        }
        catch (Exception e) {
                e.printStackTrace();
        }
    }





    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
        
    }//GEN-LAST:event_jButton1ActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextField jTextMailFrom;
    private javax.swing.JTextArea jTextMailMessage;
    private javax.swing.JTextField jTextMailSubject;
    // End of variables declaration//GEN-END:variables
    
}
