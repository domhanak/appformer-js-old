import { Menu, Toolbar } from "./Components";
import { Panel, PanelType } from "./Panel";
import { DisplayInfo } from "./DisplayInfo";
import { Part } from "./Part";
import { goTo } from "./index";
import { Component, Element } from "../core";

export abstract class Perspective extends Component {
  public af_componentId: string;
  private _af_perspectiveScreens: string[] = [];
  private _af_isDefault: boolean = false;
  private _af_isTransient: boolean = true;
  private _af_menus?: Menu[] = undefined;
  private _af_toolbar?: Toolbar = undefined;

  private _af_defaultPanelType: PanelType = PanelType.MULTI_LIST;
  private _af_displayInfo: DisplayInfo = new DisplayInfo();

  private _af_parts: Part[] = [];
  private _af_panels: Panel[] = [];

  protected constructor(componentId: string) {
    super({ type: "perspective", core_componentId: componentId });
    this.af_componentId = componentId;
  }

  public core_componentRoot(children?: any): Element {
    return this.af_componentRoot(children);
  }

  //FIXME: 1. This is only a test, remove it and create an Envelope just like it's done for Screens
  //FIXME: 2. This currently does not work with multiple instances of AppFormer
  //FIXME: 3. Execute goTo for all _components at once. This will speed up React's reconciliation
  //FIXME:    by changing the state only once.
  public core_onReady(): void {
    this._components.forEach(id => goTo(id));
  }

  public abstract af_componentRoot(children?: any): Element;

  public af_onStartup(): void {
    // TODO
  }

  public af_onOpen(): void {
    // TODO
  }

  public af_onClose(): void {
    // TODO
  }

  public af_onShutdown(): void {
    // TODO
  }

  get af_perspectiveScreens(): string[] {
    return this._af_perspectiveScreens;
  }

  set af_perspectiveScreens(value: string[]) {
    this._af_perspectiveScreens = value;
  }

  get af_isDefault(): boolean {
    return this._af_isDefault;
  }

  set af_isDefault(value: boolean) {
    this._af_isDefault = value;
  }

  get af_isTransient(): boolean {
    return this._af_isTransient;
  }

  set af_isTransient(value: boolean) {
    this._af_isTransient = value;
  }

  get af_menus(): Menu[] | undefined {
    return this._af_menus;
  }

  set af_menus(value: Menu[] | undefined) {
    this._af_menus = value;
  }

  get af_toolbar(): Toolbar | undefined {
    return this._af_toolbar;
  }

  set af_toolbar(value: Toolbar | undefined) {
    this._af_toolbar = value;
  }

  get af_defaultPanelType(): PanelType {
    return this._af_defaultPanelType;
  }

  set af_defaultPanelType(value: PanelType) {
    this._af_defaultPanelType = value;
  }

  get af_displayInfo(): DisplayInfo {
    return this._af_displayInfo;
  }

  set af_displayInfo(value: DisplayInfo) {
    this._af_displayInfo = value;
  }

  get af_parts(): Part[] {
    return this._af_parts;
  }

  set af_parts(value: Part[]) {
    this._af_parts = value;
  }

  get af_panels(): Panel[] {
    return this._af_panels;
  }

  set af_panels(value: Panel[]) {
    this._af_panels = value;
  }
}