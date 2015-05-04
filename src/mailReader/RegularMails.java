package mailReader;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.*; 
import java.util.*;
import java.io.*;
import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import JavaLib.*;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;


public class RegularMails extends javax.swing.JFrame {
    MainForm parent;
    DefaultListModel lmMails;
    Vector <SingleMail> mailList;
    
    /** Creates new form About */
    public RegularMails(MainForm parent) {
        initComponents();
        this.parent = parent;
        
        Dimension sd  = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(sd.width / 2 - this.getWidth()/ 2, sd.height / 2 - this.getHeight()/ 2);

        mailList = new Vector<SingleMail> ();
        lmMails = new DefaultListModel();

        jEditorPane1.setContentType("text/html");
        loadLists();
    }

    class CheckMailTimerTask extends TimerTask {

        public CheckMailTimerTask() {
            ;
       }

        public void addStatus(String str) {
            jTextStatus.setText(jTextStatus.getText() + "\n" + str);
        }

        public void setStatus(String str) {
            jTextStatus.setText(str);
        }

        public void run() {
            final String INBOX = "INBOX", POP_MAIL="pop3", SMTP_MAIL="smtp";
            String _smtpHost, _pop3Host, _user, _password;

            _smtpHost = "smtp.net4india.com";
            _pop3Host = "pop.net4india.com";
            _user = parent.db.emailID;
            _password = parent.db.emailPassword;

            setStatus("Connecting To Server...");
            try
            {
                Properties sysProperties = System.getProperties();
                Session session = Session.getDefaultInstance(sysProperties, null);
                Store store = session.getStore(POP_MAIL);
                addStatus("Authenticating POP3 Mail ID...");
                store.connect(_pop3Host, -1, _user, _password);
                addStatus("Done Authenticating...");

                Folder folder = store.getDefaultFolder();
                if (folder == null)
                    throw new NullPointerException("No default mail folder");

                folder = folder.getFolder(INBOX);
                if (folder == null)
                    throw new NullPointerException("Unable to get folder: " + folder);

                folder.open(Folder.READ_WRITE);
                int totalMessages = folder.getMessageCount();
                if (totalMessages == 0)
                {
                    addStatus("No Messages In Inbox!");
                    folder.close(false);
                    store.close();
                    return;
                }

                Message[] messages = folder.getMessages();
                FetchProfile fp = new FetchProfile();
                fp.add(FetchProfile.Item.ENVELOPE);
                fp.add(FetchProfile.Item.FLAGS);
                fp.add("X-Mailer");
                folder.fetch(messages, fp);

                for (int i = 0; i < messages.length; i++)
                {
                    try {
                        Address aList[] = messages[i].getFrom();

                        SingleMail sm = new SingleMail();
                        sm.from = aList[0].toString();
                        sm.subject = messages[i].getSubject();

                        if(sm.subject.equals("SECUMAIL")) {
                            continue;
                        }
                        sm.msg  = "";
                        addStatus("RECEIVED MAIL FROM: " + aList[0].toString());

                                if (messages[i].getContent() instanceof String) {
                                    sm.mime = false;
                                    sm.msg = messages[i].getContent().toString();

                                } else if (messages[i].getContent() instanceof Multipart) {
                                    System.out.println("Content Type: " + messages[i].getContentType());
                                    Multipart mp = (Multipart) messages[i].getContent();
                                    System.out.println("Count: " + mp.getCount());
                                    for (int k = 0; k < mp.getCount(); k++) {
                                        Part p = mp.getBodyPart(k);
                                        System.out.println("Part Content Type: " + p.getContentType());
                                        String type = p.getContentType();
                                        String disposition = p.getDisposition();
                                        System.out.println("Disposition: " + disposition);

                                        if ((disposition != null) && (disposition.equals("ATTACHMENT"))) {
                                            ; // do nothing for attachments...
                                        } else {
                                            if (p.getContent() instanceof String) {
                                                System.out.println("Instance of Text");
                                                if (p.getContentType().toLowerCase().contains("html")) {
                                                    System.out.println("HTML");
                                                    sm.msg = p.getContent().toString();
                                                    sm.mime = true;
                                                } else if (p.getContentType().toLowerCase().contains("text")) {
                                                    System.out.println("Plain Text");
                                                    sm.msg = p.getContent().toString();
                                                    sm.mime = false;
                                                } else {
                                                    System.out.println("Unknown Format");
                                                }
                                            } else {
                                                MimeMultipart mm = (MimeMultipart) p.getContent();
                                                System.out.println("MIME: " + mm.getCount());
                                                int cnt2 = mm.getCount();
                                                for (int j = 0; j < cnt2; j++) {
                                                    Part p2 = mm.getBodyPart(j);
                                                    if (p2.getContentType().toLowerCase().contains("html")) {
                                                        System.out.println("Instance of HTML");
                                                        sm.msg = p2.getContent().toString();
                                                        sm.mime = true;
                                                    } else if (p2.getContentType().toLowerCase().contains("text")) {
                                                        System.out.println("Plain Text");
                                                        sm.msg = p2.getContent().toString();
                                                        sm.mime = false;
                                                    } else {
                                                        sm.msg = "UNKOWN FORMAT!";
                                                        sm.mime = false;
                                                        System.out.println("Unknown Format: " + p2.getContentType());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                        try {
                            sm.dateTime.setTime(messages[i].getReceivedDate());
                        }catch(Exception e) {
                            // set current date time...
                            sm.dateTime = Calendar.getInstance();
                        }

                        if(jCheckAutoDelete.isSelected()) {
                            messages[i].setFlag(Flags.Flag.DELETED, true);
                        }

                        checkAndAdd(sm);
                        loadLists();
                    }catch(Exception e) {
                        addStatus("Exception while reading mail: " + e);
                    }
                    try { Thread.sleep(5); } catch(Exception e) { ; }
                }
                folder.close(true);
                store.close();
                addStatus("DONE!");
            }catch(Exception e) {
                addStatus("Exception: " + e);
                e.printStackTrace();
            }
        }

        public void checkAndAdd(SingleMail sm) {
            boolean found = false;

            for(int i=0;i<mailList.size();i++) {
                SingleMail existingSM = mailList.get(i);
                if(existingSM.msg.equals(sm.msg) && existingSM.from.equals(sm.from) && existingSM.subject.equals(sm.subject)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                mailList.add(sm);
            }
        }
    }


    public void loadLists() {
        lmMails.clear();
        for(int i=0;i<mailList.size();i++) {
            SingleMail sm = mailList.get(i);
            lmMails.addElement(sm.from  + ", SUB:" + sm.subject);
        }
        jListMails.setModel(lmMails);
    }

    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextStatus = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jCheckAutoDelete = new javax.swing.JCheckBox();
        jButton5 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListMails = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jLabel7 = new javax.swing.JLabel();
        jTextFrom = new javax.swing.JTextField();
        jTextSub = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(51, 51, 51));

        jPanel1.setBackground(new java.awt.Color(0, 153, 153));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton1.setText("BACK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 51, 51));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14));
        jLabel2.setForeground(new java.awt.Color(240, 240, 240));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("STATUS");
        jLabel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextStatus.setColumns(20);
        jTextStatus.setEditable(false);
        jTextStatus.setFont(new java.awt.Font("Monospaced", 0, 11));
        jTextStatus.setRows(5);
        jScrollPane1.setViewportView(jTextStatus);

        jButton2.setText("C L E A R");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                    .add(jButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(11, 11, 11)
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton2)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(0, 51, 51));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("CONTROL PANEL");
        jLabel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jButton3.setText("Refresh Inbox");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton8.setText("Delete Selected Mail");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton4.setText("Send Mail");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jCheckAutoDelete.setBackground(new java.awt.Color(0, 51, 51));
        jCheckAutoDelete.setForeground(new java.awt.Color(240, 240, 240));
        jCheckAutoDelete.setText("Auto Delete");

        jButton5.setText("Send Mails (Attachments)");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jCheckAutoDelete)
                            .add(jButton3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                            .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                            .add(jButton5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jButton4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                            .add(jButton8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jButton3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(jButton4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 27, Short.MAX_VALUE)
                .add(jCheckAutoDelete)
                .addContainerGap())
        );

        jPanel3Layout.linkSize(new java.awt.Component[] {jButton3, jButton8}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel4.setBackground(new java.awt.Color(0, 51, 51));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14));
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("DOWNLOADED MAIL LIST");
        jLabel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jListMails.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListMails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListMailsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jListMails);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(240, 240, 240));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("REGULAR MAIL CLIENT");
        jLabel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jPanel5.setBackground(new java.awt.Color(0, 51, 51));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel5.setFont(new java.awt.Font("Arial", 1, 14));
        jLabel5.setForeground(new java.awt.Color(240, 240, 240));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("FROM");
        jLabel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jScrollPane3.setViewportView(jEditorPane1);

        jLabel7.setFont(new java.awt.Font("Arial", 1, 14));
        jLabel7.setForeground(new java.awt.Color(240, 240, 240));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("SUB");
        jLabel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jTextFrom.setEditable(false);

        jTextSub.setEditable(false);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jTextFrom, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jTextSub, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
                    .add(jScrollPane3))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextFrom, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextSub, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton1)
                .addContainerGap())
        );

        new LoadForm();
        new LoadForm();

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// TODO add your handling code here:
        setVisible(false);
        parent.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        jTextStatus.setText("");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        checkMail();
    }//GEN-LAST:event_jButton3ActionPerformed

    public void checkMail() {
        java.util.Timer checkMailTimer = new java.util.Timer();
        CheckMailTimerTask checkMailTimerTask = new CheckMailTimerTask();
        checkMailTimer.schedule(checkMailTimerTask,100);
    }

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        deleteSelectedMail();
    }//GEN-LAST:event_jButton8ActionPerformed

    public void deleteSelectedMail() {
        int index = jListMails.getSelectedIndex();
        if(index==-1) {
            return;
        }
        mailList.remove(index);
        loadLists();
    }
    
    public void addStatus(String str) {
        jTextStatus.setText(jTextStatus.getText() + "\n" + str);
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        new SendMailDialog(parent).setVisible(true);
        
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jListMailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListMailsMouseClicked
        // TODO add your handling code here:
        int index = jListMails.getSelectedIndex();
        if(index<0) {
            return;
        }

        SingleMail sm = mailList.get(index);
        jTextFrom.setText(sm.from);
        jTextSub.setText(sm.subject);
        jEditorPane1.setText(sm.msg);

    }//GEN-LAST:event_jListMailsMouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        new SendMailWithAttachmentsDialog(parent).setVisible(true);
        
    }//GEN-LAST:event_jButton5ActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton8;
    public javax.swing.JCheckBox jCheckAutoDelete;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JList jListMails;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextFrom;
    public javax.swing.JTextArea jTextStatus;
    private javax.swing.JTextField jTextSub;
    // End of variables declaration//GEN-END:variables
    
}
