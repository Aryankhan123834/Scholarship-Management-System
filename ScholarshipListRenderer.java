// ScholarshipListRenderer.java
import javax.swing.*;
import java.awt.*;

public class ScholarshipListRenderer extends DefaultListCellRenderer {
@Override
public Component getListCellRendererComponent(JList<?> list, Object value, int index,
											  boolean isSelected, boolean cellHasFocus) {
	super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	if (value instanceof Scholarship) {
		Scholarship s = (Scholarship) value;
		setText("<html><b>" + s.getName() + "</b><br>" +
				"Amount: $" + s.getAmount() + "<br>" +
				"Deadline: " + s.getDeadline() + "</html>");
	}
	return this;
}
}