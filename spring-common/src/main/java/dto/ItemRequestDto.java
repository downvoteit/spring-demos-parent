package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
  private Integer id;
  private Integer categoryId;
  private String name;
  private Integer amount;
  private Double price;
}
