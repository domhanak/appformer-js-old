import { MarshallerProvider } from "../MarshallerProvider";
import * as Marshalling from "../Marshalling";
import { unmarshall } from "../Marshalling";
import { JavaInteger } from "../../java-wrappers";
import { NumValBasedErraiObject } from "../model/NumValBasedErraiObject";
import { JavaType } from "../../java-wrappers/JavaType";

const originalGetForObject = MarshallerProvider.getForObject;
const originalGetForFqcn = MarshallerProvider.getForFqcn;

afterEach(() => {
  // reset static function, otherwise other tests will be invoking the function set by this test
  MarshallerProvider.getForFqcn = originalGetForFqcn;
  MarshallerProvider.getForObject = originalGetForObject;
});

describe("marshall", () => {
  test("with regular input, should return an errai object json-string version of it", () => {
    const input = new JavaInteger("1");
    const inputErraiObject = new NumValBasedErraiObject(JavaType.INTEGER, 1).asErraiObject();

    const expectedJson = JSON.stringify(inputErraiObject);

    // skip actual marshaller implementation
    const mockedMarshaller = { marshall: jest.fn(() => inputErraiObject) };
    MarshallerProvider.getForObject = jest.fn(() => mockedMarshaller);

    // ==
    // ====== test
    const output = Marshalling.marshall(input);

    // == assertion
    expect(expectedJson).toEqual(output);
  });

  test("with null input, should return null", () => {
    const input = null as any;

    const output = Marshalling.marshall(input);

    expect(output).toBeNull();
  });

  test("with undefined input, should serialize marshaller output", () => {
    const input = undefined as any;

    const output = Marshalling.marshall(input);

    expect(output).toBeNull();
  });
});

describe("unmarshall", () => {
  test("with null input, should return undefined", () => {
    const input = null;

    const output = unmarshall(input as any, new Map());

    expect(output).toBeUndefined();
  });

  test("with undefined input, should return undefined", () => {
    const input = undefined;

    const output = unmarshall(input as any, new Map());

    expect(output).toBeUndefined();
  });

  test("with invalid json, should throw error", () => {
    const input = "{ broken json }";

    expect(() => unmarshall(input, new Map())).toThrowError();
  });

  test("with regular input, should return marshallers' specific content", () => {
    const inputJson = new NumValBasedErraiObject(JavaType.INTEGER, 1).asErraiObject();

    const expectedOutput = new JavaInteger("1");

    // skip actual marshaller implementation
    const mockedMarshaller = { unmarshall: jest.fn(() => expectedOutput) };
    MarshallerProvider.getForFqcn = jest.fn(() => mockedMarshaller);

    // ==
    // ====== test
    const output = Marshalling.unmarshall(JSON.stringify(inputJson), new Map());

    // == assertion
    expect(expectedOutput).toEqual(output);
  });
});
