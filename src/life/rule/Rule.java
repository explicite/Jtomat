package life.rule;

/**
 * Author: Jan Paw
 * Date: 4/9/12
 * Time: 6:33 PM
 */
public class Rule {
    private boolean[] rule;

    public Rule(String rule) {
        this.rule = new boolean[18];
        int offset = 0;

        for (int i = 0; i < rule.length(); i++) {
            if (rule.charAt(i) != '/')
                this.rule[Character.getNumericValue(rule.charAt(i)) + offset] = true;
            else offset = 9;
        }
    }

    public boolean getRule(int index) {
        return rule[index];
    }
}
