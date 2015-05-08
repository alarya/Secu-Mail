/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mailReader;

import java.util.Calendar;

/**
 *
 * @author Alok
 */
public class SingleMail {
    public String from, msg, subject;
    public boolean read;
    public Calendar dateTime;
    public boolean mime;

    public SingleMail() {
        from = "";
        msg = "";
        subject = "";
        read = false;
        dateTime = Calendar.getInstance();
        mime = false;
    }
}
