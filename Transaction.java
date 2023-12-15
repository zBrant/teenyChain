import java.security.*;
import java.util.ArrayList;

import javax.sound.midi.Soundbank;

public class Transaction {
  
  public String transactionID; 
  public PublicKey sender;
  public PublicKey reciepient;
  public float value;
  public byte[] signature;

  public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
  public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

  private static int sequence = 0;

  public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
    this.sender = from;
    this.reciepient = to;
    this.value = value;
    this.inputs = inputs;
  }

  private String calculateHash(){
    ++sequence;
    return StringUtil.applySha256(
      StringUtil.getStringFromKey(sender) +
      StringUtil.getStringFromKey(reciepient) +
      Float.toString(value) + sequence
    );
  }

  /* Signs all the data */
  public void generateSignature(PrivateKey privateKey){
    String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
    signature = StringUtil.applyECDSASig(privateKey, data);
  }

  public boolean verifySignature(){
    String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value);
    return StringUtil.verifyECDSASig(sender, data, signature);
  } 

  /* Returns true if new transaction could be created */
  public boolean processTransaction(){
    if(!verifySignature()){
      System.out.println("#Transaction Signature failed to verify");
      return false;
    }

    for(TransactionInput i: inputs){
      i.UTXO = TeenyChain.UTXOs.get(i.transactionOutputID);
    }

    if (getInputsValues() < TeenyChain.minimumTransaction) {
      System.out.println("#Transaction Inputs to small: " + getInputsValues());
      return false;
    }

    /* generate transaction outputs */
    float leftOver = getInputsValues() - value;
    transactionID = calculateHash();
    outputs.add(new TransactionOutput(this.reciepient, value, transactionID));
    outputs.add(new TransactionOutput(this.sender, leftOver, transactionID));

    for(TransactionOutput o: outputs){
      TeenyChain.UTXOs.put(o.id, o);
    }

    for(TransactionInput i: inputs){
      if(i.UTXO == null) continue;
      TeenyChain.UTXOs.remove(i.UTXO.id);
    }

    return true;
  }

  /* Returns sum of Inputs(UTXOs) values */
  public float getInputsValues(){
    float total = 0;
    for(TransactionInput i: inputs){
      if(i.UTXO == null) continue;
      total += i.UTXO.value;
    }
    return total;
  }
  
  /* Returns sum of outputs */
  public float getOutputValues(){
    float total = 0;
    for(TransactionOutput o: outputs){
      total += o.value;
    }
    return total;
  }
}
