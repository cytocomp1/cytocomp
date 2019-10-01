from os.path import join, realpath, dirname
from roadrunner import RoadRunner
xmod = RoadRunner(join(realpath(dirname(__file__)),'out-sbml.xml'))
# xmod.reset()
# xmod.integrator.relative_tolerance = 1e-12
# xmod.integrator.absolute_tolerance = 1e-20
# xmod.oneStep()
# xmod.simulate(0,1000,1000,selections=['time']+xmod.getFloatingSpeciesIds())
# xmod.plot()

# values = {s:xmod[s] for s in xmod.getFloatingSpeciesIds()}
# block_rates = [xmod['block{}_Add10'.format(k)] for k in (range(len(wiring.blocks)))]
# # pprint(block_rates)
# r_block_indices = model.getReactionBlockIndices()
