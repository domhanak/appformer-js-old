//
//
//
//
// FIXME: All public API methods need a revision. Their names are *temporary*.

import * as React from "react";

export const DefaultComponentContainerId = "af-js-default-screen-container";

export interface Subscriptions {
  [channel: string]: (event: any) => void;
}

export interface Service {
  [name: string]: any; //FIXME: 'any' is a baaad choice
}

export type GenericComponent = Screen | Perspective;

export type Element = React.ReactPortal | React.ReactElement<any> | HTMLElement | string;

export abstract class Perspective {
  public isReact: boolean = false;
  public af_componentId: string;
  public af_perspectiveScreens: string[];
  public af_isDefaultPerspective: boolean;

  public abstract af_perspectiveRoot(): Element;

  public has(screen: Screen | string) {
    const id = typeof screen === "string" ? screen : screen.af_componentId;
    return this.af_perspectiveScreens.indexOf(id) > -1;
  }
}

export abstract class Screen {
  public isReact: boolean = false;
  public af_componentId: string;
  public af_componentTitle?: string;
  public af_componentService: Service = {};
  public af_subscriptions: Subscriptions = {}; // FIXME: Maybe this one should be a method?

  public af_onStartup(): void {
    // FIXME: When to call?
  }

  public af_onOpen(): void {
    // TODO
  }

  public af_onFocus(): void {
    // TODO
  }

  public af_onLostFocus(): void {
    // TODO
  }

  public af_onMayClose(): boolean {
    return true;
  }

  public af_onClose(): void {
    // TODO
  }

  public af_onShutdown(): void {
    // FIXME: When to call?
  }

  public abstract af_componentRoot(): Element;

  public static containerId(af_componentId: string) {
    return `af-js-component--${af_componentId}`;
  }
}
