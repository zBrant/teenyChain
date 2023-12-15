import java.util.ArrayList;
import java.util.Date;

public class Block {

  public String hash;
  public String previousHash;
  public String merkleRoot;
  public ArrayList<Transaction> transactions = new ArrayList<>();
  private long timeStamp;
  private int nonce;
  
  public Block(String previousHash){
    this.previousHash = previousHash;
    this.timeStamp = new Date().getTime();
    this.hash = calculateHash();
  }

  public String calculateHash(){
    String calculateHash = StringUtil.applySha256(
      previousHash +
      Long.toString(timeStamp) +
      Integer.toString(nonce) +
      merkleRoot
    );
    return calculateHash;
  } 

  public void mineBlock(int difficulty){
    merkleRoot = StringUtil.getMerkleRoot(transactions);
    // String with difficulty * '0'
    String target = new String(new char[difficulty]).replace('\0', '0');
    while (!hash.substring(0, difficulty).equals(target)) {
      nonce++;
      hash = calculateHash();
    }
    System.out.println("Block Mined!! : " + hash );
  }

  public boolean addTransaction(Transaction transaction){
    if(transaction == null) return false;
    if(previousHash != "0" && transaction.processTransaction() != true){
      System.out.println("Transaction failed to process. Discated");
      return false;
    }
    
    transactions.add(transaction);
    System.out.println("Transaction Successfilly added to Block");
    return true;

  }
}
