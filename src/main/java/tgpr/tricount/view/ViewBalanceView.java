package tgpr.tricount.view;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.DialogWindow;
import tgpr.tricount.controller.Balance_controller;
import tgpr.tricount.controller.Participant;
import tgpr.tricount.model.Operation;
import tgpr.tricount.model.Repartition;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.model.User;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewBalanceView extends DialogWindow {


    private Tricount tricount;
    private List<Operation> operationList = new ArrayList<>();
   private User user;
    private List<Participant> participantList = new ArrayList<>();

    private Balance_controller controller;
    private Button btnClose;

    public ViewBalanceView(Balance_controller controller, Tricount tricount, User user) {
        super("Balance");
        this.controller = controller;
        this.tricount = tricount;
        this.user = user;
        Panel root = new Panel().asGridPanel(3);
        operationList = tricount.getOperations();
        DecimalFormat df = new DecimalFormat("#.00");

        for(User use : tricount.getParticipants()){
            participantList.add(new Participant(0, use.getId()));
        }
        setBalance();

        for(Participant participant : participantList ) {
            new GridLayout(participantList.size()).setHorizontalSpacing(0).setVerticalSpacing(0);

            if(participant.getBalance()<0) {
                String formattedNumber = String.format("%.2f", participant.getBalance());
                Label labelbalance =  new Label(formattedNumber + " €");
                root.addComponent(labelbalance);
                labelbalance.setBackgroundColor(TextColor.ANSI.RED_BRIGHT);
                root.addComponent(new Label("   | "));
                root.addComponent(new Label(user.getFullnameById(participant.getIdParticipant())));

            }else{

                root.addComponent(new Label(user.getFullnameById(participant.getIdParticipant())));
                root.addComponent(new Label("   | "));
                String formattedNumber = String.format("%.2f", participant.getBalance());
                Label labelbalance =  new Label(formattedNumber + " €");
                root.addComponent(labelbalance);
                labelbalance.setBackgroundColor(TextColor.ANSI.GREEN_BRIGHT);
            }
        }new EmptySpace().addTo(root);
        setHints(List.of(Hint.CENTERED,Hint.FIXED_SIZE));
        setCloseWindowWithEscape(true);
        setFixedSize(new TerminalSize(30,5));
            createButtonPanel().addTo(root);
        setComponent(root);
    }

    private Panel createButtonPanel(){
        var panel = Panel.horizontalPanel(1).center();
        btnClose = new Button ("Close",this::close).addTo(panel);
        btnClose.takeFocus();
        return panel;
    }

    public void setBalance(){
        for (Operation operation : operationList){
            for(Participant participant : participantList){
                if(operation.getInitiator().getId() == participant.getIdParticipant()){
                    participant.setBalance(participant.getBalance()+ operation.getAmount());
                }
            }setBalanceParticipant(operation);
        }
    }
    public void setBalanceParticipant(Operation operation){
        for(Repartition repartition: operation.getRepartitions()){
            int sumRep = Repartition.totalRepartition(operation.getId());
            double payToPayByParticipant = operation.getAmount() / sumRep;
            for(Participant participant : participantList){
                if(repartition.getUser().getId() == participant.getIdParticipant()){
                    participant.setBalance(participant.getBalance()- payToPayByParticipant * repartition.getWeight());
                }
            }
        }
    }

}

