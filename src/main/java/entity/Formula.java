package entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Formula {

  private String formula;
  private List<String> inputs;

  private String outputKey;

  private String type;

  private String keyJumper;

  private double valueJumper;


}
