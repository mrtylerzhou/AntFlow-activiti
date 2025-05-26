package org.openoa.engine.utils;

import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.el.JuelExpression;
import org.activiti.engine.impl.juel.*;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;

public class MultiInstanceUtils {
    public static String getCollectionNameByBehavior(ActivityBehavior activityBehavior) {
        JuelExpression juelExpression = (JuelExpression)((MultiInstanceActivityBehavior) activityBehavior).getCollectionExpression();

        TreeValueExpression valueExpression = (TreeValueExpression)juelExpression.valueExpression;
        ExpressionNode node = valueExpression.getNode();
        AstNode child = ((AstEval) node).getChild();
        String name = ((AstIdentifier) child).getName();
        return name;
    }
}
