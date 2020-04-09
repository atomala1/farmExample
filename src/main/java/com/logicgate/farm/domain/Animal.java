package com.logicgate.farm.domain;

import java.io.Serializable;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "animal")
public class Animal implements Serializable {

  private static final long serialVersionUID = 7241520989988001549L;

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  protected Long id;

  @ManyToOne
  @JoinColumn(name = "barn_id")
  protected Barn barn;

  @Column(name = "name")
  protected String name;

  @Column(name = "favorite_color")
  protected Color favoriteColor;

  protected Animal() {
    // default constructor
  }

  public Animal(String name, Color favoriteColor) {
    this.name = name;
    this.favoriteColor = favoriteColor;
  }

  public Animal(String name, Color favoriteColor, Barn barn) {
    this(name, favoriteColor);
    this.barn = barn;
  }

  public Long getId() {
    return id;
  }

  public Barn getBarn() {
    return barn;
  }

  public Animal setBarn(Barn barn) {
    this.barn = barn;
    return this;
  }

  public String getName() {
    return name;
  }

  public Color getFavoriteColor() {
    return favoriteColor;
  }

  @Override
  public String toString() {
    return String.format("Animal[id=%d, name=%s, favoriteColor=%s]", id, name, favoriteColor.name());
  }

  @Override
  public boolean equals(Object obj) {
    return Optional.ofNullable(obj).isPresent()
      && (obj == this || (obj.getClass() == getClass() && ((Animal) obj).getId().equals(getId())));
  }

}
