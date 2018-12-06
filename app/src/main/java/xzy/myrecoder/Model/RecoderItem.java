package xzy.myrecoder.Model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RecoderItem{

   @Id private Long itemNum;
    private String itemName;
    private String itemSize;
    private String itemLength;
    private String date;
    private boolean flag;//收藏
   @Generated(hash = 431123532)
   public RecoderItem(Long itemNum, String itemName, String itemSize,
           String itemLength, String date, boolean flag) {
       this.itemNum = itemNum;
       this.itemName = itemName;
       this.itemSize = itemSize;
       this.itemLength = itemLength;
       this.date = date;
       this.flag = flag;
   }
   @Generated(hash = 485752623)
   public RecoderItem() {
   }
   public Long getItemNum() {
       return this.itemNum;
   }
   public void setItemNum(Long itemNum) {
       this.itemNum = itemNum;
   }
   public String getItemName() {
       return this.itemName;
   }
   public void setItemName(String itemName) {
       this.itemName = itemName;
   }
   public String getItemSize() {
       return this.itemSize;
   }
   public void setItemSize(String itemSize) {
       this.itemSize = itemSize;
   }
   public String getItemLength() {
       return this.itemLength;
   }
   public void setItemLength(String itemLength) {
       this.itemLength = itemLength;
   }
   public String getDate() {
       return this.date;
   }
   public void setDate(String date) {
       this.date = date;
   }
   public boolean getFlag() {
       return this.flag;
   }
   public void setFlag(boolean flag) {
       this.flag = flag;
   }
  
    
}
