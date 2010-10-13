package org.chartsy.rsi;

import java.beans.PropertyEditorSupport;

public class SourceDatasetPropertyEditor extends PropertyEditorSupport
{

    @Override
    public String[] getTags()
    {
        return new String[]
                {
                    "Close Price", "HMA"
                };
    }

    @Override
    public void setAsText(String s)
    {
        if (s.equals("Close Price"))
        {
            setValue(new Integer(IndicatorProperties.SOURCE_CLOSE));
        } else if (s.equals("HMA"))
        {
            setValue(new Integer(IndicatorProperties.SOURCE_HMA));
        } else
        {
            throw new IllegalArgumentException(s);
        }
    }

    @Override
    public String getAsText()
    {
        switch (((Number) getValue()).intValue())
        {
            default:
            case IndicatorProperties.SOURCE_CLOSE:
                return "Close Price";
            case IndicatorProperties.SOURCE_HMA:
                return "HMA";
        }
    }
}
