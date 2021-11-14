import java.util.ArrayList;
import java.util.List;

public class MediatorPattern {
    public static void main(String[] args) {
        TradingSite InGameTradingSite = new TradingSite();
        Customer xXFallenAngelXx = new Customer("xXFallenAngelXx", 1000, InGameTradingSite);
        Customer KamilSlimak2007PL = new Customer("KamilSlimak2007PL", 980, InGameTradingSite);
        Customer DefinitelyNotAScammer = new Customer("DefinitelyNotAScammer",10000, InGameTradingSite);

        xXFallenAngelXx.possessions.add(new Item("Epic Sword","Super hard to get sword"));
        DefinitelyNotAScammer.possessions.add(new Item("Epic Sword", "Super hard to get sword. Yes yes, very hard, definitely not a cheap replica"));

        xXFallenAngelXx.listAnItem(xXFallenAngelXx.possessions.get(0),1000);
        DefinitelyNotAScammer.listAnItem(DefinitelyNotAScammer.possessions.get(0),970);

        Item targetItem = InGameTradingSite.availableOffers.get(0).item;

        KamilSlimak2007PL.buyAnItem(targetItem);

        targetItem = InGameTradingSite.availableOffers.get(1).item;
        KamilSlimak2007PL.buyAnItem(targetItem);

        targetItem = InGameTradingSite.availableOffers.get(0).item;
        DefinitelyNotAScammer.cancellAListing(targetItem);
        xXFallenAngelXx.cancellAListing(targetItem);
    }
}
interface Mediator{
    void list(Item item, Customer seller, int price);
    void buy(Item item, Customer buyer);
    void cancelListing(Item item, Customer seller);
}

class Item{
    String name;
    String description;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }
}

class Customer{
    String name;
    int balance;
    List<Item> possessions;
    Mediator mediator;

    public Customer(String name,int balance, Mediator mediator) {
        this.name = name;
        this.balance = balance;
        this.possessions = new ArrayList<>();
        this.mediator = mediator;
    }

    public void notify(String message){
        System.out.println("["+name+"]: "+message);
    }

    public void listAnItem(Item item, int price){
        mediator.list(item,this, price);
    }

    public void buyAnItem(Item item){
        mediator.buy(item,this);
    }

    public void cancellAListing(Item item){
        mediator.cancelListing(item, this);
    }
}


class TradingSite implements Mediator{
    List<Listing> availableOffers;

    public TradingSite() {
        this.availableOffers = new ArrayList<>();
    }

    @Override
    public void list(Item item, Customer seller, int price) {
        if(seller.possessions.contains(item)){
            availableOffers.add(
                    new Listing(item, price, seller)
            );
            seller.possessions.remove(item);
            seller.notify("Listing created successfully");
        } else {
            seller.notify("You do not own the item");
        }
    }

    @Override
    public void buy(Item item, Customer buyer) {
        for(Listing listing : availableOffers){
            if(listing.item==item){
                if(buyer.balance>= listing.price){
                    buyer.balance -= listing.price;
                    buyer.possessions.add(item);
                    listing.owner.balance += listing.price;

                    listing.owner.notify(buyer.name+" has bought the "+item.name+" from you");
                    buyer.notify("You have bought the "+item.name+" from "+listing.owner.name);

                    availableOffers.remove(listing);
                } else {
                    buyer.notify("You do not have enough money to buy "+item.name +" from "+listing.owner.name);
                }
                return;
            }
        }
        buyer.notify("Item could not be found.");
    }

    @Override
    public void cancelListing(Item item, Customer seller) {
        for(Listing listing : availableOffers) {
            if (listing.item == item) {
                if(seller == listing.owner) {
                    availableOffers.remove(listing);
                    seller.possessions.add(item);
                    seller.notify("Listing cancelled");
                } else {
                    seller.notify("You are not the owner of this item");
                }
                return;
            }
        }
        seller.notify("This item is not listed");
    }
}

class Listing{
    Item item;
    Customer owner;
    int price;
    public Listing(Item item, int price, Customer owner) {
        this.item = item;
        this.price = price;
        this.owner = owner;
    }
}
