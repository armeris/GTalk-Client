package org.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

public class XMPP {

    private static XMPPConnection connection = null;

    /**
     * @param args
     */
    public static boolean connect(String user, String password) {
        ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com",5222,"gmail.com");
        config.setDebuggerEnabled(false);
        config.setReconnectionAllowed(true);

        connection = new XMPPConnection(config);

        try{
            connection.connect();
            System.out.println(">>> Connected to "+ connection.getHost());
        }catch(XMPPException e){
            return false;
        }

        try{
            connection.login(user, password);
            System.out.println(">>> Connected as "+ connection.getUser());
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
        }catch(XMPPException e){
            return false;
        }

        return true;
    }

    public static boolean disconnect(){
       if(connection!=null){
           connection.disconnect();
           connection = null;
           return true;
       }else{
           return false;
       }
    }

    private static Chat openConversation(String user){

        if(connection!=null && connection.isAuthenticated()){
            ChatManager chatManager = connection.getChatManager();
            MessageListener listener = new MessageListener() {
                @Override
                public void processMessage(Chat arg0, Message arg1) {
                }
            };
            Chat chat = chatManager.createChat(user, listener);
            return chat;
        }else{
            return null;
        }
    }

    public static boolean chat(String user, String message){
        Chat chat = null;
        if((chat=openConversation(user))!=null){
            try{
                Message msg = new Message(user, Message.Type.chat);
                msg.setBody(message);
                chat.sendMessage(msg);
                return true;
            }catch (XMPPException e){
                return false;
            }
        }else{
            return false;
        }
    }

    public static Collection<RosterEntry> getContacts(){
        Collection<RosterEntry> result = new ArrayList<RosterEntry>();

        if(connection!=null && connection.isAuthenticated()){
            Roster roster = connection.getRoster();
            if(roster!=null){
                result = roster.getEntries();
            }
        }
        return result;
    }

    public static boolean isConnected(String user){
        if(connection!=null && connection.isAuthenticated()){
            Roster roster = connection.getRoster();
            Presence presence = roster.getPresence(user);
            if(presence.isAvailable()){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public static void setConnection(XMPPConnection connection) {
        XMPP.connection = connection;
    }

    public static XMPPConnection getConnection() {
        return connection;
    }
}
