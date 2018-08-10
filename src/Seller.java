public class Seller implements ISellAuction {
    @Override
    public SellerEntity CallSell(Double ps, int os, boolean s) {
        SellerEntity SE =  new SellerEntity();
        SE.setPs(ps);
        SE.setOs(os);
        SE.setS(s);
        return SE;
    }
}
