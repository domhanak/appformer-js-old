import {MarshallingContext} from "../MarshallingContext";
import {JavaInteger} from "../../java-wrappers";
import {NullableMarshaller} from "./NullableMarshaller";

export class JavaIntegerMarshaller extends NullableMarshaller<JavaInteger, number> {
  public notNullMarshall(input: JavaInteger, ctx: MarshallingContext): number {
    return input.get();
  }
}
