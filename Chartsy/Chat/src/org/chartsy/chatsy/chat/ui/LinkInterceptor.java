package org.chartsy.chatsy.chat.ui;

import java.awt.event.MouseEvent;

public interface LinkInterceptor
{

    public boolean handleLink(MouseEvent mouseEvent, String link);

}
