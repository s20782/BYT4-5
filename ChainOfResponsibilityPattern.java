import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ChainOfResponsibilityPattern{
    static JTextArea display = new JTextArea();

    public static void main(String[] args) {
        Calculator calc = new Calculator( new Input(),new Operation(),new Result(), new Clear());

        JFrame frame = new JFrame("Simple calculator with chain of responsibility");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        display.setSize(300,50);
        display.setEditable(false);
        display.setFont(display.getFont().deriveFont(30f));
        frame.getContentPane().add(BorderLayout.NORTH,display);

        //input
        JButton button1 = new JButton("1");
        JButton button2 = new JButton("2");
        JButton button3 = new JButton("3");
        JButton button4 = new JButton("4");
        JButton button5 = new JButton("5");
        JButton button6 = new JButton("6");
        JButton button7 = new JButton("7");
        JButton button8 = new JButton("8");
        JButton button9 = new JButton("9");
        JButton button0 = new JButton("0");

        //operators
        JButton buttonAdd = new JButton("+");
        JButton buttonSub = new JButton("-");
        JButton buttonMult = new JButton("*");
        JButton buttonDiv = new JButton("/");

        //result
        JButton buttonEq = new JButton("=");
        JButton buttonClear = new JButton("C");

        JPanel buttons = new JPanel(new GridLayout(4,4));
        JButton[] buttonArray = new JButton[]{
                button7, button8,button9,buttonDiv,
                button4,button5,button6,buttonMult,
                button1,button2,button3,buttonSub
                ,buttonClear,button0,buttonAdd,buttonEq
        };
        addAllToPanel(buttons, buttonArray);
        addListenerToAll(buttonArray,calc);
        frame.add(buttons);
        frame.setVisible(true);
        frame.setSize(new Dimension(300,350));
    }
    static void addAllToPanel(JPanel panel, JButton[] buttons){
        for(JButton button: buttons){
            panel.add(button);
        }
    }

    static void addListenerToAll(JButton[] buttons,Calculator calc){
        for(JButton button: buttons){
            String textOnButton = button.getActionCommand();
            if(Arrays.asList(new String[]{"/","+","-","*"}).contains(textOnButton)){
                button.addActionListener(e -> {
                    calc.first.execute(new Request(textOnButton, RequestType.OPERATION));
                    display.setText(calc.updateDisplay());
                });

            } else if (textOnButton.equals("=")){
                button.addActionListener(e -> {
                    calc.first.execute(new Request("",RequestType.EQUAL));
                    display.setText(calc.updateDisplay());
                });
            } else if (textOnButton.equals("C")){
                button.addActionListener(e -> {
                    calc.first.execute(new Request("",RequestType.CLEAR));
                    display.setText(calc.updateDisplay());
                });
            } else {
                button.addActionListener(e -> {
                    calc.first.execute(new Request(textOnButton,RequestType.INPUT));
                    display.setText(calc.updateDisplay());
                });
            }
        }
    }
}

enum RequestType{
    OPERATION,
    INPUT,
    EQUAL,
    CLEAR
}

interface Link{
    void setNextLink(Link nextLink);
    void execute(Request request);
}

class Request{
    public Request(String value, RequestType requestType) {
        Value = value;
        this.requestType = requestType;
    }
    String Value;
    RequestType requestType;
}

class Calculator{
    public static String number1;
    public static String number2;
    public static String op;
    public static String display;
    Link first;

    public static String updateDisplay(){
        if(!op.equals("")){
            display = number1 + " " + op + " " + number2;
        } else {
            display = number1;
        }
        return display;
    }
    public Calculator(Link link1, Link ... links) {
        number1 = "";
        number2 = "";
        op = "";
        display = "";
        first = link1;
        if(links.length>0) {
            first.setNextLink(links[0]);
            for (int i = 0; i<links.length-1;i++){
                links[i].setNextLink(links[i+1]);
            }
        }
    }
}

class Input implements Link{
    private Link nextLink;
    @Override
    public void setNextLink(Link nextLink) {
        this.nextLink = nextLink;
    }

    @Override
    public void execute(Request request) {
        if(request.requestType == RequestType.INPUT){
            if(Calculator.op.equals("")){
                Calculator.number1 += request.Value;
            } else {
                Calculator.number2 += request.Value;
            }
        } else if (nextLink != null){
            nextLink.execute(request);
        } else {
            System.out.println("Wrong request");
        }
    }
}

class Clear implements Link{
    private Link nextLink;
    @Override
    public void setNextLink(Link nextLink) {
        this.nextLink = nextLink;
    }

    @Override
    public void execute(Request request) {
        if(request.requestType == RequestType.CLEAR){
            Calculator.number1 = "";
            Calculator.number2 = "";
            Calculator.op = "";
            Calculator.display = "";
        } else if (nextLink != null){
            nextLink.execute(request);
        } else {
            System.out.println("Wrong request");
        }
    }
}

class Operation implements Link{
    private Link nextLink;
    @Override
    public void setNextLink(Link nextLink) {
        this.nextLink = nextLink;
    }

    @Override
    public void execute(Request request) {
        if(request.requestType == RequestType.OPERATION && !Calculator.number1.equals("")){
            if(Calculator.op.equals("") || Calculator.number2.equals("")){
                Calculator.op = request.Value;
            } else {
                request.requestType=RequestType.EQUAL;
                nextLink.execute(request);
            }
        } else if (nextLink != null){
            nextLink.execute(request);
        } else {
            System.out.println("Wrong request");
        }
    }
}

class Result implements Link{
    private Link nextLink;
    @Override
    public void setNextLink(Link nextLink) {
        this.nextLink = nextLink;
    }

    @Override
    public void execute(Request request) {
        if(request.requestType == RequestType.EQUAL){
            int number1 = Integer.parseInt(Calculator.number1);
            int number2;
            try {
                number2 = Integer.parseInt(Calculator.number2);
            } catch(NumberFormatException e){
                number2 = 0;
            }

            if(Calculator.op.equals("+")){
                Calculator.number1 = String.valueOf(number1 + number2);
                Calculator.op = request.Value;
                Calculator.number2 = "";
            } else if(Calculator.op.equals("-")){
                Calculator.number1 = String.valueOf(number1 - number2);
                Calculator.op = request.Value;
                Calculator.number2 = "";
            } else if(Calculator.op.equals("*")){
                Calculator.number1 = String.valueOf(number1 * number2);
                Calculator.op = request.Value;
                Calculator.number2 = "";
            } else if(Calculator.op.equals("/")){
                if(number2==0){
                    Calculator.number1 = "0";
                } else {
                    Calculator.number1 = String.valueOf(number1 / number2);
                }
                Calculator.op = request.Value;
                Calculator.number2 = "";
            }
        } else if (nextLink != null){
            nextLink.execute(request);
        } else {
            System.out.println("Wrong request");
        }
    }
}