package thi_ltm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Server extends javax.swing.JFrame {

    ServerSocket ss;
    HashMap listClient = new HashMap();
    int count = 0;

    public Server() {
        try {
            initComponents();
            listClientJoined.setEditable(false);
            ss = new ServerSocket(1108);
            this.lblStatus.setText("Server Started.");

            new ClientAccept().start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class ClientAccept extends Thread {

        public void run() {
            while (true) {
                try {
                    Socket s = ss.accept(); //server chap nhan ket noi tu client qua port 1108
                    String username = new DataInputStream(s.getInputStream()).readUTF();
                    if (listClient.containsKey(username)) {     //kiem tra username da ton tai trong hashmap hay chua
                        DataOutputStream out = new DataOutputStream(s.getOutputStream());
                        DataInputStream in = new DataInputStream(s.getInputStream());
                        out.writeUTF("Username Are Already Registered !!!");
                        out.flush();
                    } else if (count >= 2) {
                        DataOutputStream out = new DataOutputStream(s.getOutputStream());
                        out.writeUTF("#limit");
                        out.flush();
                    } else {
                        DataOutputStream out = new DataOutputStream(s.getOutputStream());
                        DataInputStream in = new DataInputStream(s.getInputStream());
                        count++;
                        
                        listClient.put(username, s);
                        listClientJoined.append(username + " joined \n");   //hien thi ds client joined
                        if(count > 1){//moi them vao//////////////////
                            out.writeUTF("#option" + username + ";" + s); 
                        }///////////////////////
                        out.writeUTF("");

                        new MsgRead(s, username, out, in).start();
                        new PrepareClientList().start();
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    class MsgRead extends Thread {

        Socket s;
        String username;
        DataOutputStream out;
        DataInputStream in;

        private MsgRead(Socket s, String username, DataOutputStream out, DataInputStream in) {
            this.s = s;
            this.username = username;
            this.out = out;
            this.in = in;
        }

        public void run() {
            while (!listClient.isEmpty()) {
                try {
                    DataInputStream in = new DataInputStream(s.getInputStream());
                    String i = in.readUTF();
                    if (i.equals("disconnected")) {//nguoi dung tat cua so window
                        listClient.remove(username);
                        listClientJoined.append(username + " disconnected! \n");
                        count--;

                        new PrepareClientList().start();
                        Set<String> k = listClient.keySet();    //lay key cua hashmap
                        Iterator itr = k.iterator();    //duyet tung phan tu cua Set
                        while (itr.hasNext()) { //kiem tra co con phan tu tiep theo khong
                            String key = (String) itr.next();
                            if (!key.equalsIgnoreCase(username)) {  //kiem tra chuoi khong phan biet chu hoa va chu thuong
                                try {
                                    new DataOutputStream(((Socket) listClient.get(key)).getOutputStream()).writeUTF(i);//gui disconnected de xoa lich su chat
                                } catch (Exception e) {
                                    System.out.println(e);
//                                    listClient.remove(key);
//                                    listClientJoined.append(key + ": disconnected!");
//                                    new PrepareClientList().start();
                                }
                            }
                        }
                        in.close();
                        out.close();
                        s.close();
                    } else if (i.contains("#user")) {
                        i = i.substring(5);
                        StringTokenizer st = new StringTokenizer(i, ":");//cat chuoi theo dau :
                        String otherUser = st.nextToken();
                        String content = st.nextToken();
                        try {
                            new DataOutputStream(((Socket) listClient.get(otherUser)).getOutputStream()).writeUTF("< " + username + " to " + otherUser + " > " + content);
                        } catch (Exception e) {
                            //System.out.println(e);
                            listClient.remove(otherUser);
                            listClientJoined.append(otherUser + ": disconnected!");
                            new PrepareClientList().start();
                        }

                    } else {//chat All
//                        Set k = listClient.keySet();
//                        Iterator itr = k.iterator();
//                        while (itr.hasNext()) {
//                            String key = (String) itr.next();
//                            if (!key.equalsIgnoreCase(username)) {
//                                try {
//                                    new DataOutputStream(((Socket) listClient.get(key)).getOutputStream()).writeUTF("< " + username + " to All >" + i);
//                                } catch (Exception e) {
//                                    //System.out.println(e);
//                                    listClient.remove(key);
//                                    listClientJoined.append(key + ": disconnected!");
//                                    new PrepareClientList().start();
//                                }
//                            }
//                        }
                    }
                } catch (Exception ex) {
                    try {
                        in.close();
                        out.close();
                        s.close();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }
        }
    }

    class PrepareClientList extends Thread {

        public void run() {
            try {
                String users = "";
                Set userKey = listClient.keySet();
                Iterator itr = userKey.iterator();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    users += key + ",";
                }

                if (users.length() != 0) {
                    users = users.substring(0, users.length() - 1);// ds user
                }
                itr = userKey.iterator();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    try {
                        new DataOutputStream(((Socket) listClient.get(key)).getOutputStream()).writeUTF("#listUser" + users);
                    } catch (Exception e) {
                        System.out.println(e);
//                        listClient.remove(key);
//                        listClientJoined.append(key+ ": removed!");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listClientJoined = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        listClientJoined.setColumns(20);
        listClientJoined.setRows(5);
        jScrollPane1.setViewportView(listClientJoined);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Status:");

        lblStatus.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lblStatus.setText(".........");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblStatus))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTextArea listClientJoined;
    // End of variables declaration//GEN-END:variables
}
