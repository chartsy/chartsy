package org.chartsy.chatsy.chatimpl.profile;

import org.jivesoftware.smackx.packet.VCard;

public interface VCardListener
{
    void vcardChanged(VCard vcard);
}
