public class Buyer implements IBuyAuction {

    @Override
    public BuyerEntity CallBuy(Double pb, int ob, boolean b) {
        BuyerEntity BE =  new BuyerEntity();
        BE.setPb(pb);
        BE.setOb(ob);
        BE.setB(b);
        return BE;
    }
}
