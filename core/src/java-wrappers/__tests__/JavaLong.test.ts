import { JavaLong } from "../JavaLong";
import { BigNumber } from "bignumber.js";

describe("get", () => {
  describe("with valid input", () => {
    test("positive integer, should return same value as number", () => {
      const input = "12";

      const output = new JavaLong(input).get();

      expect(output).toEqual(new BigNumber("12"));
    });

    test("negative integer, should return same value as number", () => {
      const input = "-12";

      const output = new JavaLong(input).get();

      expect(output).toEqual(new BigNumber("-12"));
    });
  });

  test("with invalid textual string, should return NaN", () => {
    const input = "abc";

    const output = new JavaLong(input).get();

    expect(output).toEqual(new BigNumber(NaN));
  });

  describe("with input in the numeric bounds", () => {
    describe("minimum bound", () => {
      test("equals, should return same value as number", () => {
        const input = `${JavaLong.MIN_VALUE}`;

        const output = new JavaLong(input).get();

        expect(output).toEqual(new BigNumber(`${JavaLong.MIN_VALUE}`));
      });

      test("less than, should return NaN", () => {
        const input = new BigNumber(`${JavaLong.MIN_VALUE}`).minus(1, 10);

        const output = new JavaLong(input.toString(10)).get();

        expect(output).toEqual(new BigNumber(NaN));
      });
    });

    describe("maximum bound", () => {
      test("equals, should return same value as number", () => {
        const input = `${JavaLong.MAX_VALUE}`;

        const output = new JavaLong(input).get();

        expect(output).toEqual(new BigNumber(`${JavaLong.MAX_VALUE}`));
      });

      test("greater than, should return NaN", () => {
        const input = new BigNumber(`${JavaLong.MAX_VALUE}`).plus(1, 10);

        const output = new JavaLong(input.toString(10)).get();

        expect(output).toEqual(new BigNumber(NaN));
      });
    });
  });

  describe("with float string", () => {
    test("positive with decimal closest to 0, should return truncated value as Number", () => {
      const input = "12.1";

      const output = new JavaLong(input).get();

      expect(output).toEqual(new BigNumber("12"));
    });

    test("positive with decimal exactly between 0 and 1, should return truncated value as Number", () => {
      const input = "12.5";

      const output = new JavaLong(input).get();

      expect(output).toEqual(new BigNumber("12"));
    });

    test("positive with decimal closest to 1, should return truncated value as Number", () => {
      const input = "12.9";

      const output = new JavaLong(input).get();

      expect(output).toEqual(new BigNumber("12"));
    });

    test("negative with decimal closest to 0, should return truncated value as Number", () => {
      const input = "-12.1";

      const output = new JavaLong(input).get();

      expect(output).toEqual(new BigNumber("-12"));
    });

    test("negative with decimal exactly between 0 and 1, should return truncated value as Number", () => {
      const input = "-12.5";

      const output = new JavaLong(input).get();

      expect(output).toEqual(new BigNumber("-12"));
    });

    test("negative with decimal closest to 1, should return truncated value as Number", () => {
      const input = "-12.9";

      const output = new JavaLong(input).get();

      expect(output).toEqual(new BigNumber("-12"));
    });
  });
});

describe("set", () => {
  test("with valid direct value, should set", () => {
    const input = new JavaLong("1");
    expect(input.get()).toEqual(new BigNumber("1"));

    input.set(new BigNumber("2"));

    expect(input.get()).toEqual(new BigNumber("2"));
  });

  test("with invalid direct value, should set NaN", () => {
    const input = new JavaLong("1");
    expect(input.get()).toEqual(new BigNumber("1"));

    input.set(new BigNumber(`${JavaLong.MAX_VALUE}`).plus(1, 10));

    expect(input.get()).toEqual(new BigNumber(NaN));
  });

  test("with valid value from function, should set", () => {
    const input = new JavaLong("1");
    expect(input.get()).toEqual(new BigNumber("1"));

    input.set(cur => new BigNumber("2").plus(cur));

    expect(input.get()).toEqual(new BigNumber("3"));
  });

  test("with invalid value from function, should set NaN", () => {
    const input = new JavaLong(`${JavaLong.MAX_VALUE}`);
    expect(input.get()).toEqual(new BigNumber(`${JavaLong.MAX_VALUE}`));

    input.set(cur => new BigNumber(`${JavaLong.MAX_VALUE}`).plus(cur));

    expect(input.get()).toEqual(new BigNumber(NaN));
  });
});

describe("doubleValue", () => {
  test("should convert successfully", () => {
    const input = new JavaLong("1");

    const output = input.doubleValue();

    expect(output.get()).toBe(1);
  });
});

describe("intValue", () => {
  test("should convert successfully", () => {
    const input = new JavaLong("1");

    const output = input.intValue();

    expect(output.get()).toBe(1);
  });
});

describe("shortValue", () => {
  test("should convert successfully", () => {
    const input = new JavaLong("1");

    const output = input.shortValue();

    expect(output.get()).toBe(1);
  });
});

describe("byteValue", () => {
  test("should convert successfully", () => {
    const input = new JavaLong("1");

    const output = input.byteValue();

    expect(output.get()).toBe(1);
  });
});

describe("floatValue", () => {
  test("should convert successfully", () => {
    const input = new JavaLong("1");

    const output = input.floatValue();

    expect(output.get()).toBe(1);
  });
});

describe("longValue", () => {
  test("should convert successfully", () => {
    const input = new JavaLong("1");

    const output = input.longValue();

    expect(output.get().toNumber()).toBe(1);
  });
});

describe("_fqcn", () => {
  test("must be the same than in Java", () => {
    const fqcn = (new JavaLong("1") as any)._fqcn;

    expect(fqcn).toBe("java.lang.Long");
  });
});
