import java.security.PublicKey;

public class TransactionOutput {
  public String id;
  public PublicKey reciepient;
  public float value;
  public String parentTransactionID;

  public TransactionOutput(PublicKey reciepient, float value, String parentTransactionID){
    this.value = value;
    this.parentTransactionID = parentTransactionID;
    this.reciepient = reciepient;
    this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient) + Float.toString(value) + parentTransactionID);
  }

  public boolean isMine(PublicKey publicKey){
    return publicKey == reciepient;
  }
}
