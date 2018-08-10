import java.util.*;

public class Main {

    public static void main(String[] args) {
        Buyer buyer = new Buyer();
        Seller seller = new Seller();

        Vector<SellerEntity> sellerOrders  = new Vector<>();
        Vector<BuyerEntity> buyerOrders  = new Vector<>();

        for (int i=0;i<10;i++) {
            sellerOrders.add(seller.CallSell((1+Math.random()*10), (int)(1+Math.random()*10), true));
            buyerOrders.add(buyer.CallBuy((1+Math.random()*10), (int)(1+Math.random()*10), false));
        }

        for (SellerEntity se:sellerOrders){
            //System.out.println(se.getPs()+":"+se.getOs()+":"+se.getS());
        }
        for (BuyerEntity be:buyerOrders){
            //System.out.println(be.getPb()+":"+be.getOb()+":"+be.getB());
        }

        // 执行最大成交量原则，获取最大成交量和备选成交价集合
        // 2.1 提取全体价格，降序
        Vector<Double> prices = new Vector<>();
        for (SellerEntity se:sellerOrders){
            prices.add(se.getPs());
        }
        for (BuyerEntity be:buyerOrders){
            prices.add(be.getPb());
        }
        Collections.sort(prices, Collections.reverseOrder());
        System.out.println("提取全体价格，降序"+prices);

        Map priceOfQ = new HashMap();
        Map priceOfQView = new HashMap();

        for (Double price: prices){
            Map tmpView = new HashMap();
            int[] tmp = new int[2];
            // CBQ 累积买单数量
            int Ob=0;
            for (BuyerEntity be:buyerOrders) {
                if (be.getPb()>=price){
                    Ob=Ob+be.getOb();
                }
            }
            tmpView.put("CBQ",Ob);
            tmp[0] = Ob;

            // CSQ 累积卖单数量
            int Os=0;
            for (SellerEntity se:sellerOrders) {
                if (se.getPs()<=price){
                    Os=Os+se.getOs();
                }
            }
            tmpView.put("CSQ",Os);
            tmp[1] = Os;

            priceOfQ.put(price,tmp);
            priceOfQView.put(price,tmpView);
        }
        //System.out.println(priceOfQ);
        System.out.println("累积买卖单量"+priceOfQView);

        // 2.2 每个价位的最大成交量
        Map dealOfQ = new HashMap();
        Iterator iter = priceOfQ.entrySet().iterator();
        Double price;
        int mev;
        while (iter.hasNext()){
            Map.Entry entry= (Map.Entry)iter.next();
            price = (Double) entry.getKey();
            mev = Arrays.stream((int[])entry.getValue()).min().getAsInt();
            dealOfQ.put(price,mev);
        }
        System.out.println("每个价位的最大成交量"+dealOfQ);

        // 2.3 该次集合成交量的最大量及价格集合
        Object[] tmp = dealOfQ.values().toArray();
        Arrays.sort(tmp,Collections.reverseOrder());
        int MV = (int)tmp[0];

        Iterator iter2 = dealOfQ.entrySet().iterator();
        Double price2;
        ArrayList<Double> dealOfP =new ArrayList<>();
        while (iter2.hasNext()){
            Map.Entry entry = (Map.Entry)iter2.next();
            price2 = (Double)entry.getKey();
            if ((int)entry.getValue()==MV){
                dealOfP.add(price2);
            }
        }
        System.out.println("最大量"+MV);
        System.out.println("价格集合"+dealOfP);

        // 2.4 筛选集合dealOfP，若大小为1，则直接输出，否则执行如下逻辑
        if (dealOfP.size()==1){
            System.out.println(dealOfP.get(0));
            return;
        }

        // 执行最小剩余原则，获得成交价集合
        // 3.1 计算每个价格上最小未成交量
        Map LV = new HashMap();
        Map priceOfStressMaxCBQ =new HashMap();
        Map priceOfStressMinCSQ =new HashMap();
        ArrayList<Double> priceOfStress =new ArrayList<>();
        int CBQ_CSQ[];
        for (Double p:dealOfP){
            if (priceOfQ.containsKey(p)) {
                CBQ_CSQ = (int[]) priceOfQ.get(p);
                LV.put(p, Math.abs((CBQ_CSQ[0]-CBQ_CSQ[1])));
                //为4.1做准备
                if(CBQ_CSQ[0]>CBQ_CSQ[1]){
                    priceOfStressMaxCBQ.put(p,CBQ_CSQ[0]);
                }else if(CBQ_CSQ[0]<CBQ_CSQ[1]){
                    priceOfStressMinCSQ.put(p,CBQ_CSQ[1]);
                }
            }
        }
        System.out.println("每个价格上最小未成交量"+LV);
        Object[] posMax = priceOfStressMaxCBQ.keySet().toArray();
        Arrays.sort(posMax,Collections.reverseOrder());
        if (posMax.length>0) {
            priceOfStress.add((Double) posMax[0]);
        }

        Object[] posMin = priceOfStressMinCSQ.keySet().toArray();
        Arrays.sort(posMin);
        if (posMin.length>0) {
            priceOfStress.add((Double) posMin[0]);
        }

        // 3.2 筛选集合LV，若大小为1，则直接输出，否则执行如下逻辑
        if (LV.size()==1){
            System.out.println(LV.get(0));
            return;
        }

        // 执行市场压力原则，获得成交价集合（一定只剩两个单）
        // 4.1
        System.out.println("市场压力获得成交价集合"+priceOfStress);
        if (priceOfStress.size()==1){
            System.out.println(priceOfStress.get(0));
            return;
        }

        // 执行参考价格原则，获得基于参考价格原则的最终唯一的成交价
        // 由于没有历史数据，假设上次最终成交价pt=0;
        Double pt=0d;
        ArrayList<Double> dealOfPs =new ArrayList<>();
        for (Double maxMinPrice:priceOfStress){
            dealOfPs.add(Math.abs(maxMinPrice-pt));
        }
        Collections.sort(dealOfPs);
        System.out.println("参考价格"+dealOfPs.get(0));

        Double f = Math.round(dealOfPs.get(0) * 1000) * 0.001d;
        System.out.println("参考价格精度"+f);
    }
}
