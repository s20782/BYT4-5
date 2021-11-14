import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class TemplatePattern {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("For lazy exponential calculation without gui type: \"1\"");
        System.out.println("For proper exponential calculation with gui type: \"2\"");
        if(scanner.nextInt()==1){
            CalculationTemplate lec = new LazyExponentialCalculation();
            lec.calculateCalculation();
        } else {
            CalculationTemplate proper = new ProperExponentialCalculationWithGui();
            proper.calculateCalculation();
        }
    }
}

abstract class CalculationTemplate{
    double number1;
    double number2;
    double result;

    public void calculateCalculation() {
        inputNumbers();
        calculateResult();
        returnResult();
    }

    public abstract void inputNumbers();
    public abstract void calculateResult();
    public abstract void returnResult();
}

class LazyExponentialCalculation extends CalculationTemplate{

    @Override
    public void inputNumbers() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is your number?");
        number1 = scanner.nextDouble();
        System.out.println("What power of "+number1+ " do you want?");
        try{
        number2 = scanner.nextInt();
        } catch (Exception e){
            System.out.println("This must be an integer");
        }

    }

    @Override
    public void calculateResult() {
        result = 1;
        for(int i = 0; i< number2;i++){
            result *= number1;
        }
    }

    @Override
    public void returnResult() {
        System.out.println("Result is "+ result);
    }
}

class ProperExponentialCalculationWithGui extends CalculationTemplate{
    JFrame frame;
    JPanel line;
    JButton submit;
    public ProperExponentialCalculationWithGui() {
        frame = new JFrame();
        line = new JPanel(new FlowLayout());
        frame.setSize(300,150);
        frame.getContentPane().add(BorderLayout.NORTH,line);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
    }

    @Override
    public void inputNumbers() {
        AtomicBoolean finished = new AtomicBoolean(false);
        JTextField input1 = new JTextField("  ");
        input1.setPreferredSize(new Dimension(30,30));
        JTextArea text = new JTextArea("to the power of");
        JTextField input2 = new JTextField("  ");
        input2.setPreferredSize(new Dimension(30,30));
        submit = new JButton("Submit");
        submit.addActionListener(e -> {
            try {
                number1 = Double.parseDouble(input1.getText());
                number2 = Double.parseDouble(input2.getText());
                finished.set(true);
            } catch (NumberFormatException nfe){
                input1.setText("  ");
                input2.setText("  ");
                System.out.println(nfe);
            }

        });
        line.add(input1);
        line.add(text);
        line.add(input2);
        frame.getContentPane().add(BorderLayout.CENTER,submit);
        frame.setSize(300,120);
        while(!finished.get()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie){

            }
        }
    }

    @Override
    public void calculateResult() {
        result = Math.pow(number1, number2);
    }

    @Override
    public void returnResult() {
        frame.remove(line);
        frame.remove(submit);
        line = new JPanel(new FlowLayout());
        line.add(new JTextArea("Result is "+result));
        frame.getContentPane().add(BorderLayout.CENTER,line);
        frame.setSize(300,80);
    }
}
