package tgpr.tricount.model;

import org.springframework.util.Assert;
import tgpr.framework.Model;
import tgpr.framework.Params;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Tricount extends Model {
    public enum Fields {
        Id, Title, Description, CreatedAt, Templates, Creator
    }



    public Tricount(){

    }
    public Tricount(String title, int creatorId) {
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.creatorId = creatorId;
    }


  /*  public Tricount(String title, String description, int creatorId) {
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.creatorId = creatorId;
    }*/
  public Tricount(String title, String text, int creatorId) {
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.creatorId = creatorId;
    }

    public Tricount(String title, String description) {
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.creatorId = creatorId;
    }


    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Operation> getOperations() {
        return queryList(Operation.class, "select * from operations where tricount=:id order by operation_date desc",
                new Params("id", id));
    }

    public List<Subscription> getSubscriptions() {
        return queryList(Subscription.class, "select * from subscriptions s where tricount=:id",
                new Params("id", id));
    }

    public List<User> getParticipants() {
        return queryList(User.class, """
                        select u.* from subscriptions s
                        join users u on s.user=u.id
                        where tricount=:id
                        """,
                new Params("id", id));
    }

    public List<Template> getTemplates() {
        return queryList(Template.class, """
                        select * from templates
                        where tricount=:id
                        """,
                new Params("id", id));
    }

    private String title;
    private double expenses;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    private int creatorId;

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public User getCreator() {
        return User.getByKey(creatorId);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tricount tricount = (Tricount) o;
        return id == tricount.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tricount[" +
                "id=" + id +
                ", title=" + title +
                ", description=" + description +
                ", createdAt=" + createdAt +
                ", creatorId=" + creatorId +
                "]";
    }

    @Override
    protected void mapper(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        title = rs.getString("title");
        description = rs.getString("description");
        createdAt = rs.getObject("created_at", LocalDateTime.class);
        creatorId = rs.getInt("creator");
    }

    @Override
    public void reload() {
        reload("select * from tricounts where id=:id",
                new Params("id", id));
    }

    public static Tricount getByKey(int id) {
        return queryOne(Tricount.class, "select * from tricounts where id=:id",
                new Params("id", id));
    }


    public static Tricount getByTitleAndUser(String title, User user) {
        return queryOne(Tricount.class, "select * from tricounts where title=:title and creator=:user",
                new Params("title", title).add("user", user.getId()));
    }

    public static List<Tricount> getAll() {
        return queryList(Tricount.class, "select * from tricounts");
    }

    public Tricount save() {
        int c;
        Tricount obj = getByKey(id);
        String sql;
        var params = new Params()
                .add("id", id)
                .add("title", title)
                .add("description", description)
                .add("created_at", createdAt)
                .add("creator", creatorId);
        if (obj == null) {
            sql = "insert into tricounts (title,description,created_at,creator) " +
                    "values (:title,:description,:created_at,:creator)";
            int id = insert(sql, params);
            if (id > 0)
                this.id = id;
        } else {
            sql = "update tricounts set title=:title," +
                    "description=:description," +
                    "created_at=:created_at," +
                    "creator=:creator " +
                    "where id=:id";
            c = execute(sql, params);
            Assert.isTrue(c == 1, "Something went wrong");
        }
        return this;
    }

    public void delete() {
        int c = execute("delete from tricounts where id=:id",
                new Params("id", id));
        Assert.isTrue(c == 1, "Something went wrong");
    }
    public double getTotalOfTricounts(){
        double total = 0;
        for (int i = 0;i < getOperations().size();i++){
            total+= getOperations().get(i).getAmount();
        }
        return total;
    }

    public double getBalance(User user){
        double balance = 0;
        for (int i = 0;i < getOperations().size();i++){
            if(getOperations().get(i).getInitiator()== Security.getLoggedUser()){
                balance += getOperations().get(i).getAmount();
            }
        }
        return balance - getMyExpense(user);

    }

    public double getMyExpense(User user){
        double myExpense = 0;
        for (int i = 0;i < getOperations().size();i++){
            myExpense+= computeUserExpense(getOperations().get(i).getAmount(), getOperations().get(i).getRepartitions(),user);
        }
        return myExpense;
    }

    public double computeUserExpense(double amount,List<Repartition> listUser, User user){
        int poids = 0;
        double expense = 0;
        for (int i = 0;i < listUser.size();i++){
            poids+= listUser.get(i).getWeight();
        }
        for (int i = 0;i < listUser.size();i++){
            if(listUser.get(i).getUserId() == user.getId()){
                var sousTotal = amount * listUser.get(i).getWeight();
                expense+= sousTotal/poids;
            }
        }
        return expense;
    }

    public int getNumberOfParticipants() {
        List<User> participants = queryList(User.class, """
                                        select u.* from subscriptions s
                                        join users u on s.user=u.id
                                        where tricount=:id
                                        """,
                new Params("id", id));
        return participants.size();
    }
    public static List<Tricount> getAllTricounts(int shift, int id, String searchString) {
        return queryList(Tricount.class,
                "SELECT * FROM tricounts t WHERE t.creator = :id AND t.id IN(" +
                        "SELECT t.id FROM tricounts t WHERE t.title LIKE :search)" +
                        "OR t.id IN(" +
                        "SELECT t.id FROM tricounts t WHERE t.description LIKE :search)" +
                        "OR t.id IN(" +
                        "SELECT t.id FROM tricounts t, subscriptions s, users u WHERE " +
                        "t.id = s.tricount AND s.user = u.id AND u.full_name LIKE :search)" +
                        "AND t.id IN(" +
                        "   SELECT t.id FROM tricounts t, subscriptions s WHERE " +
                        "t.id = s.tricount AND s.user = :id) " +
                        "ORDER BY t.created_at DESC " +
                        "LIMIT :shift, 12",
                new Params("id", id).add("shift", shift).add("search", "%" + searchString + "%"));
    }
    public String NumbersOfFriends() {
        String res = "";
        int numberOfParticipants = getNumberOfParticipants();

        if (numberOfParticipants == 0) {
            res = "with no friend";
        }
         else if (numberOfParticipants == 1){
            res = "with no friend";
        }

         else {
            --numberOfParticipants;
            res = "with " + numberOfParticipants + " friends";
        }

        return res;
    }
    public String showDescription(){
        String res = "";
        if (this.description == null){
            res = "No description";
        }
        else
            res = this.description;
        return res;
    }
    public String getNameOfCreator() {
        return "Created by " + User.getByKey(creatorId).toString();
    }
    public static Integer getCount(int id) {
        return queryScalar(Integer.class, "SELECT COUNT(*) result FROM tricounts t WHERE t.id IN(" +
                        "SELECT t.id FROM tricounts t, subscriptions s WHERE " +
                        "t.id = s.tricount AND s.user = :id)",
                new Params("id", id));
    }
}
