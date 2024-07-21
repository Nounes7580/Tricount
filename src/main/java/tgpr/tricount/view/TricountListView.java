package tgpr.tricount.view;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;

import java.util.ArrayList;
import java.util.List;
import tgpr.framework.*;
import com.googlecode.lanterna.gui2.menu.Menu;
import com.googlecode.lanterna.gui2.menu.MenuBar;
import com.googlecode.lanterna.gui2.menu.MenuItem;
import tgpr.tricount.controller.TricountListController;
import tgpr.tricount.controller.ViewTricountController;
import tgpr.tricount.model.Security;
import tgpr.tricount.model.Tricount;
import tgpr.tricount.model.User;

public class TricountListView extends BasicWindow {

    private final TricountListController controller;
    private List<Tricount> list;
    private List<CardView> cardList ;
    private final TextBox txtFilter = new TextBox(new TerminalSize(30,1));
    private final Menu menuFile = new Menu("File");
    private final Panel root;
    private final Panel body = new Panel(new GridLayout(3));
    private Paginator paginator;
    private int id = Security.getLoggedUserId();
    private User user = User.getByKey(id);

    public TricountListView(TricountListController controller) {
        this.controller = controller;
        this.cardList = new ArrayList<>();

        root = new Panel().setLayoutManager(new GridLayout(1));
        setComponent(root);
        setTitle(getTitleWithUser());
        setHints(List.of(Hint.EXPANDED));


        root.addComponent(highPnael());

        list = TricountListController.getAllCard(0, id, txtFilter.getText());

        root.addComponent(body);
        fillPanel();
        //root.addComponent(cardsPanel());
        new EmptySpace().addTo(body);

        root.addComponent(bottomPanel());
    }
    private Panel highPnael(){
        Panel highPanel = new Panel().setLayoutManager(new LinearLayout(Direction.VERTICAL));
        createMenu().addTo(highPanel);
        filterPanel().addTo(highPanel);
        new EmptySpace().addTo(highPanel);

        return highPanel;
    }
    private MenuBar createMenu(){
        MenuBar menuBar = new MenuBar();

        menuBar.add(menuFile);
        MenuItem menuLogout = new MenuItem("Logout", controller::logout);
        menuFile.add(menuLogout);
        MenuItem menuExit = new MenuItem("Exit", controller::exit);
        menuFile.add(menuExit);
        MenuItem menuViewProfile = new MenuItem("View Profile", controller::viewProfile);
        menuFile.add(menuViewProfile);

        return menuBar;
    }
    private Panel filterPanel(){
        Panel filter = new Panel();

        filter.setLayoutManager(new GridLayout(2));
        filter.addComponent(new Label("Filter:"));
        txtFilter.setTextChangeListener((txt, byUser) -> reloadForSearch());
        filter.addComponent(txtFilter);

        return filter;
    }
    private void fillPanel() {
        for (int i = 0; i < list.size(); ++i) {
            //les labels du tricount
            Label titleLabel = new Label(list.get(i).getTitle() + "\n");
            Label descriptionLabel = new Label(list.get(i).showDescription() + "\n");
            Label creatorNameLabel = new Label(list.get(i).getNameOfCreator() + "\n");
            Label participantsLabel = new Label(list.get(i).NumbersOfFriends() + "\n");
            //le button open
            //Button openButton = new Button("Open",this.controller::openTricount);
            Tricount tricount = Tricount.getByKey(list.get(i).getId());

            Button openButton = new Button(" Open ", () -> controller.openTricount(tricount));

            Panel cardPanel = Panel.verticalPanel(LinearLayout.Alignment.Center).sizeTo(34, 5);

            cardPanel.addComponent(titleLabel.setForegroundColor(TextColor.ANSI.BLUE).center());
            cardPanel.addComponent(descriptionLabel.setForegroundColor(new TextColor.RGB(128, 128, 128)).addStyle(SGR.ITALIC).center());
            cardPanel.addComponent(creatorNameLabel.center());
            cardPanel.addComponent(participantsLabel.center());
            cardPanel.addComponent(openButton.center());

            body.addComponent(cardPanel.withBorder(Borders.singleLine()));
        }
    }


    private Panel cardsPanel() {
        fillCardList();
        Panel cardPanel = Panel.verticalPanel(LinearLayout.Alignment.Center).sizeTo(34, 5);
        for (int i = 0; i < cardList.size(); ++i) {
            CardView cardView = cardList.get(i);
            cardPanel.addComponent((Component) cardView);
        }
        return cardPanel;
    }
    /*private void fillCardList(){
        CardView cards = (CardView) user.getTricounts();
        for (int i = 0; i < this.cardList.size(); ++i) {
            this.cardList.add(i,cards);
        }
    }*/
    private void fillCardList() {
        List<Tricount> tricounts = user.getTricounts();
        //this.cardList.clear(); // Clear existing elements before adding new ones

        for (Tricount tricount : tricounts) {
            CardView cardView = new CardView(tricount); // Assuming there's a constructor in CardView that accepts Tricount
            this.cardList.add(cardView);
        }
    }

    private Panel bottomPanel(){
        Panel bottomPanel = new Panel().setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

        //ajout le boutton Create a new Tricount au bas gauche de bottomPanel
        new Button("Create a new Tricount", this.controller::addTricount).addTo(bottomPanel).bottomLeft();
        //paginateur
        paginator = new Paginator(this, 12, this::pageChange);
        paginator.setCount(TricountListController.getCount(id));
        //boucle for pour ajouter d'espaces entre mon boutton et le paginateur
        for (int i = 0; i < 19; ++i) {
            new EmptySpace().addTo(bottomPanel);
        }
        bottomPanel.addComponent(paginator);

        return bottomPanel;
    }

    private void pageChange(int page) {
        reload();
    }
    private void reloadForSearch() {
        body.removeAllComponents();
        list = TricountListController.getAllCard(0, id, txtFilter.getText());
        fillPanel();
    }
    private void reload() {
        body.removeAllComponents();
        list = TricountListController.getAllCard(paginator.getPage() * 12 , id, txtFilter.getText());
        fillPanel();
    }


    private String getTitleWithUser() {
        return "Tricount (" + Security.getLoggedUser().getMail() + " - " + (Security.isAdmin() ? "Admin" : "User") + ")";
    }

}





