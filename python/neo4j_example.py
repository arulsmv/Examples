#!/usr/bin/python
import argparse
from py2neo import Graph, Node, Relationship, Path

class GraphBuilder:
  def __init__(self, password):
    self.graph = Graph(password=password)
    self.graph_data = None
 

  def commit_graph(self):
    self.graph.create(self.graph_data)
 
  def build_graph(self):
    def new_person(pname):
      return Node("Person", name=pname)
    alice = new_person("alice")
    bob = new_person("bob")
    self.graph_data = Relationship(alice, "KNOWS", bob)
    self.commit_graph()

def main():
  argparser = argparse.ArgumentParser("Builds the diff graphis between two persons")
  argparser.add_argument("--password", default="neo4j")
  args = argparser.parse_args()
  graph_builder = GraphBuilder(args.password)
  graph_builder.build_graph()


if __name__ == '__main__':
  main()
