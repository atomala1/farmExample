package com.logicgate.farm.domain;

import com.logicgate.farm.util.FarmUtils;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "barn")
public class Barn implements Serializable {

  private static final long serialVersionUID = -4177424470187511682L;

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @Column(name = "name")
  protected String name;

  @Column(name = "color")
  protected Color color;

  @Column(name = "capacity")
  protected Integer capacity = FarmUtils.barnCapacity();

  protected Barn() {
    // default constructor
  }

  public Barn(String name, Color color) {
    this.name = name;
    this.color = color;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Color getColor() {
    return color;
  }

  public Integer getCapacity() {
    return capacity;
  }

  @Override
  public String toString() {
    return String.format("Barn[id=%d, name=%s, color=%s, capacity=%d]", id, name, color.name(), capacity);
  }

  @Override
  public boolean equals(Object obj) {
    return Optional.ofNullable(obj).isPresent()
      && (obj == this || (obj.getClass() == getClass() && ((Barn) obj).getId().equals(getId())));
  }

}
